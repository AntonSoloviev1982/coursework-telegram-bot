package pro.sky.courseworktelegrambot.services;

//import lombok.extern.slf4j.Slf4j;

import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.CatRepository;
import pro.sky.courseworktelegrambot.repositories.DogRepository;
import pro.sky.courseworktelegrambot.repositories.StateRepository;
import pro.sky.courseworktelegrambot.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
//интересно, что доступ к сервису TelegramBotSender осуществляется без внедрения
//а как к родителю. Не думал, что Spring соберет эту матрешку из сервисов
public class TelegramBot extends TelegramBotSender {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final UserRepository userRepository;
    private final StateRepository stateRepository;
    private final ShelterService shelterService;

    //для тестов оставим возможность заинжектить сюда
    //реальные сервисы с моками после создания spyTelegramBot
    //final - отсутствует
    private FeedbackRequestService feedbackRequestService;
    private MessageToVolunteerService messageToVolunteerService;
    private AdoptionService adoptionService;
    private ReportService reportService;
    private final DogRepository dogRepository;
    private final CatRepository catRepository;

    public TelegramBot(UserRepository userRepository,
                       StateRepository stateRepository,
                       ShelterService shelterService,
                       FeedbackRequestService feedbackRequestService,
                       MessageToVolunteerService messageToVolunteerService,
                       AdoptionService adoptionService,
                       ReportService reportService,
                       DogRepository dogRepository,
                       CatRepository catRepository) {
        this.userRepository = userRepository;
        this.stateRepository = stateRepository;
        this.shelterService = shelterService;
        this.feedbackRequestService = feedbackRequestService;
        this.messageToVolunteerService = messageToVolunteerService;
        this.adoptionService = adoptionService;
        this.reportService = reportService;
        this.dogRepository = dogRepository;
        this.catRepository = catRepository;
    }

    //для тестов
    public void setServices( AdoptionService adoptionService,
                             ReportService reportService,
                             FeedbackRequestService feedbackRequestService,
                             MessageToVolunteerService messageToVolunteerService) {
        this.feedbackRequestService = feedbackRequestService;
        this.messageToVolunteerService = messageToVolunteerService;
        this.adoptionService = adoptionService;
        this.reportService = reportService;
    }

    private JpaRepository<? extends Pet, Integer> petRepository(ShelterId shelterId) {
        return (shelterId == ShelterId.DOG) ? dogRepository : catRepository;
    }

    private State initialState;  //начальное состояние для новых пользователей извлечем заранее,
    //остальные будут приходить вместе с пользователем при извлечении его из репозитория
    private State badChoiceState;  //если пришло сообщение, не соответствующее кнопкам
    private State afterShelterChoiceState;  //состояние после выбора приюта.

    @PostConstruct
    public void initStates() {
        initialState = stateRepository.findByNamedState(NamedState.INITIAL_STATE);//.get();
        badChoiceState = stateRepository.findByNamedState(NamedState.BAD_CHOICE);//.get();
        afterShelterChoiceState = stateRepository.findByNamedState(NamedState.AFTER_SHELTER_CHOICE_STATE);//.get();
        //прикрепим кнопки к начальному состоянию. Названия вынем из приютов
        List<StateButton> buttons = new ArrayList<>();
        AtomicInteger column= new AtomicInteger(1);
        shelterService.findAll().forEach(shelter -> {
            buttons.add(new StateButton(
                    initialState, shelter.getName(), afterShelterChoiceState,
                    (byte)1, (byte)column.getAndIncrement(), null));
        });
        initialState.setButtons(buttons);
    }

    @Override
    //TelegramBotSender, чтобы стать реальным классом, уже переопределил этот метод пустышкой
    //теперь в этом потомке сделаем это по-настоящему
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {return;}
        Message message = update.getMessage();
        long chatId = message.getChatId();
        User user = userRepository.findById(chatId).orElse(null);
        State oldState = null; //старое состояние (или состояние при входе)
        if (user == null) {
            user = new User(chatId, message.getChat().getFirstName(), initialState);
            //oldState останется = null. Это вызовет goToNextState, т.к. oldState<>initialState
            try {
                //я не обрабатываю ошибку внутри sendMessage, а намеренно выбрасываю ее в бот
                //чтобы была возможность на нее правильно среагировать, например не сохранять состояние
                sendMessage(user.getId(), "Привет, " + user.getName(), null, 0);
            } catch (TelegramApiException e) {
                logger.error("Ошибка посылки приветственного сообщения: " + e.getMessage());
                return;
            }
        } else {
            //запоминаем старое состояние (или состояние при входе)
            oldState = user.getState();
            //Последующие действия возможно назначат новое состояние
            if (oldState.isTextInput()) {
                //в сообщении может не быть текста и message.getText() будет null
                if (RETURN_BUTTON_FOR_TEXT_INPUT.equals(message.getText())) {
                    user.setState(user.getPreviousState());
                } else {
                    try {
                        //хорошо бы сделать рефлексией, т.е. поместить имена методов в табл State
                        switch (user.getState().getNamedState()) {
                            //проверяем, не нужны ли спец действия для определенных состояний
                            case MESSAGE_TO_VOLUNTEER -> createMessageToVolonteer(user, message);
                            case FEEDBACK_REQUEST -> createFeedbackRequest(user, message);
                            case REPORT -> acceptReport(user, message);
                            case ANIMAL_BY_NUMBER -> showAnimal(user, message);
                        }
                    } catch (TelegramApiException e) {
                        //при невозможности послать ответ, ничего не делаем. Но прерываем выполнение метода
                        //в логах останется запись от sendMessage
                        return;
                    }
                }
            } else {
                //проверяем, не нажата ли кнопка. Если нажата, то только установим новое состояние у user
                checkButton(user, message);
            }
        }
        //У usera возможно установлено новое состояние.
        //Если оно изменилось относительно входного, отработаем изменение
        //Здесь посылаем сообщение с кнопками из StateButton
        //Если в новом состоянии кнопок нет, то новое состояние вернем в состояние oldState
        try {
            if (!user.getState().equals(oldState)) {goToNextState(user, oldState);}
        } catch (TelegramApiException e) {
            //при невозможности послать ответ, ничего не делаем. Но прерываем выполнение метода
            //в логах останется запись от sendMessage
            return;
        }

        //сохраняем новые состояния пользователя (старое и новое)
        //PreviousState меняем, если к выходу State отличется от входного
        //Т.е. в ожидательных состояниях PreviousState не трогается, пока мы не нажмем "Возврат к боту"
        if (!user.getState().equals(oldState)) user.setPreviousState(oldState);
        user.setStateTime();
        userRepository.save(user);
    }

    private void goToNextState(User user, State oldState) throws TelegramApiException {
        //если сменилось состояние
        State state = user.getState(); //новое состояние. Если нет кнопок, то мы его откатим к oldState
        String text = state.getText(); //предстоящее сообщение - текст нового состояния

        //Дальше текст может быть подменен в специальных случаях
        if (state.getNamedState()==NamedState.REPORT) {
            text = reportRequestText(user, oldState, null);
        }
        if (text.startsWith("@")) {
            ShelterId shelterId = user.getShelterId();
            String informationType = text.substring(1);
            try {
                text = shelterService.getInformation(shelterId, informationType);
            } catch (IllegalAccessException e) {
                //Антон, может быть здесь InformationTypeByShelterNotFound?
                throw new RuntimeException(e);
            }
        }

        //выясняем, есть ли кнопки в текущем состоянии
        List<StateButton> buttons = state.getButtons();
        if (buttons.isEmpty()  && !state.isTextInput() && state.getNamedState()!=NamedState.INITIAL_STATE) {
            //если кнопок нет и не состояние текстового ввода и не список приютов
            //то возвращаем состояние назад (к тому, что было при входе в обработку сообщения)
            //этот режим используем для вывода разной информации о приюте, оставаясь в прежнем состоянии
            user.setState(oldState);
            //выводим текст нового и кнопки старого состояния
        }
        //бросает TelegramApiException
        sendMessageToUser(user, text, 0);
        if (state.getNamedState()==NamedState.ANIMAL_LIST) {showAnimalList(user);}  //пока ничего не делает
    }

    private void checkButton(User user, Message message) {
        //для состояний, не являющихся текстовым вводом, т.е. состояний выбора из клавиатуры
        //задача: проверить что пришло (какая кнопка нажата) и установить новое состояние
        if (!message.hasText()) {
            user.setState(badChoiceState);
            return;
        }
        String textFromUser = message.getText();

        if (user.getState().equals(initialState)) {  //если состоялся выбор приюта
            //Это заплатка. По хорошему надо найти по названию ключ из таблицы приютов
            if (!textFromUser.equals("Собаки") & !textFromUser.equals("Кошки")) {
                user.setState(badChoiceState);
                return;
            }
            user.setShelterId((textFromUser.equals("Собаки"))?ShelterId.DOG:ShelterId.CAT);
            user.setState(afterShelterChoiceState);
            return;
        }
        List<StateButton> buttons = user.getState().getButtons();
        for (StateButton button : buttons) {
            if (button.getCaption().equals(textFromUser)){
                user.setState(button.getNextState());
                return;
            }
        }
        user.setState(badChoiceState);
    }


    private void createMessageToVolonteer(User user, Message message) {
        if (!message.hasText()) {
            user.setState(badChoiceState);
            return;
        }
        //сохраняем в табл MessageToVolonteer пришедший текст
        messageToVolunteerService.createMessageToVolunteer(message.getMessageId(), user, message.getText());
        //message.getMessageId() - тоже сохраняем
        //чтобы у волонтера была возможность ответить на конкретный вопрос, а не просто послать сообщение

        //состояние не меняем. Пользователь может слать следующие сообщения волонтеру.
        //поэтому потом goToNextState не выполняется и user.setPreviousState тоже не выполняется
    }

    private void createFeedbackRequest(User user, Message message) {
        if (!message.hasText()) {
            user.setState(badChoiceState);
            return;
        }

        //сохраняем в табл FeedbackRequest пришедший текст
        feedbackRequestService.createFeedbackRequest(user, message.getText());

         user.setState(user.getPreviousState());
        //состояние изменилось, поэтому вызовется goToNextState
        //и нарисует состояние до входа в запрос обратной связи
        try {
            sendMessage(user.getId(), "Запрос обратной связи принят. Волонтер свяжется с вами указанным способом.", null, 0);
        } catch (TelegramApiException e) {
            logger.error("Не удалось отправить подтверждение на прием запроса обратной связи." + e.getMessage());
            //Главное - запрос принят. Ничего не делаем
            //даже не сообщаем в вызывающий метод
        }
    }

    private void acceptReport(User user, Message message) {
        byte[] photo = null; //пока так
        String  text = null;
        String mediaType = null;
        int mediaSize = 0;
        if (message.hasPhoto()) {
            List<PhotoSize> photoSizes = message.getPhoto();
            PhotoSize largestPhoto = photoSizes.get(photoSizes.size() - 1); // получаем последний и самый большой вариант фото
            String fileId = largestPhoto.getFileId();
            //String path = largestPhoto.getFilePath(); не работает, приходит null
            ResponseEntity<String> response = getFilePath(fileId);
            System.out.println(response);
            if (response.getStatusCode() == HttpStatus.OK){
                JSONObject jsonObject = new JSONObject(response.getBody());
                String filePath = String.valueOf(jsonObject
                        .getJSONObject("result")
                        .getString("file_path"));
                System.out.println(jsonObject);
                photo = downloadPhoto(filePath);
            }
            // Определяем MIME-тип
            mediaType = detectMimeType(photo);
            System.out.println(mediaType);
            // Размер файла
            mediaSize = largestPhoto.getFileSize();
            System.out.println(mediaSize);
        }
        if (message.hasText()) {
            //text = message.getDocument().toString().getBytes(); //пока так
            text = message.getText();
            System.out.println(text);
        }

        Report report = null;
        //найдем активное усыновление пользователя
        Adoption adoption = adoptionService.getActiveAdoption(user, LocalDate.now());
        if (adoption != null) { //если усыновление найдено
            report = reportService.saveReport(adoption, LocalDate.now(), photo,mediaType,mediaSize, text);
        }
        //если после сохранения report=null, значит у юзера не было испытательного срока
        //в этом случае reportRequestText побочным действием вернет его предыдущее состояние
        String requestText = reportRequestText(user, user.getPreviousState(), report);

        //используем sendMessageToUser, а не sendMessage, чтобы не смахнуть кнопку Возврат к кнопкам
        try {
            sendMessageToUser(user, requestText, 0);
        } catch(TelegramApiException e) {
            //если не удалось послать подтверждение приема, то ничего страшного.
            //Главное - отчет принят. Ничего не делаем
            //даже не сообщаем в вызывающий метод
        }


        //состояние не меняем. Пользователь может слать следующие элементы отчета волонтеру.
        //поэтому потом goToNextState не выполняется и user.setPreviousState тоже не выполняется
    }
    //вспомогательный метод получения пути файла по fileId
    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(
                getFileInfoUri(),
                HttpMethod.GET,
                request,
                String.class,
                getBotToken(),
                fileId);
    }
    //вспомогательный метод скачивания файла по пути
    private byte[] downloadPhoto(String filePath) {
        String fullUri = getFileStorageUri().replace("{token}", getBotToken())
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
            System.out.println(urlObj);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка создания URI", e);
        }

        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения с URI" + urlObj.toExternalForm(), e);
        }
    }



    private String detectMimeType(byte[] data) {
        // Регистрируем MimeDetector
        MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());

        // Получаем MIME-тип из массива байт
        String mimeType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(data)).toString();

        return mimeType;
    }

    private void showAnimalList(User user) throws TelegramApiException  {
        //вызывается после вывода сообщения состояния AnimalList - Наши питомцы
        String searchTerm; //можно запросить условия отбора, но пока не сделали
        //посылаем картинки из базы. Пока только toString животных.
        String text = petRepository(user.getShelterId()).findAll().toString();
        sendMessageToUser(user, text, 0);
    }

    private void showAnimal (User user, Message message) throws TelegramApiException {
        //id животного спрятано в message
        //посылаем все о животном с помощью sendMessage

        if (!message.hasText()) {
            sendMessageToUser(user, "Ожидаю номер животного:", 0);
            return;
        }
        String text = message.getText();
        //проверить, что число
        int id = Integer.parseInt(text);
        JpaRepository<? extends Pet, Integer> repository = petRepository(user.getShelterId());
        Pet pet = repository.findById(id).orElse(null);

        if (pet == null) {
            sendMessageToUser(user, "Неправильный номер", 0);
        } else {
            sendMessageToUser(user, pet.toString(), 0);
        }
        //В конце используем sendMessageToUser, а не sendMessage, чтобы не смахнуть кнопку Возврат к кнопкам
        sendMessageToUser(user, "Введите следующий номер животного:", 0);
        //состояние не меняем. Пользователь может слать следующие ID животных.
        //поэтому потом goToNextState не выполняется и user.setPreviousState тоже не выполняется
    }

    //Текст, отправляемый пользователю, с запросом того, что еще осталось прислать
    //метод вызывается после приема отчета, а также если пользователь выбрал кнопку Сдать отчет
    private String reportRequestText(User user, State oldState, Report report) {
        //oldState - состояние при входе в обработчик сообщений бота
        //Если поиск активного усыновления будет неудачным, то вернемся в него
        //Если report<>null, т.е. отчет сдан до какой-то степени, то oldState будет не востребован

        //Пользователь выбрал, что хочет сдать отчет (report=null)
        //или чего-то прислал в состоянии сдачи отчета (report<>null)
        //что будем ему писать?

        if (report == null) {  //состояние сдачи отчета - неизвестно. Извлечем его из базы
            Adoption adoption = adoptionService.getActiveAdoption(user, LocalDate.now());
            if (adoption == null) {
                user.setState(oldState);
                return "В приюте " + shelterService.getNameById(user.getShelterId())
                        + " у Вас нет активного испытательного срока";
            }
            report = reportService.getReportForToday(adoption);
        }
        //если отчет в базе не найден, значит он еще не сдан. report будет == null

        //потом узнаем состояние сдачи отчета и сформируем сообщение
        if (report==null || !report.getPhotoPresented() && !report.getPhotoPresented()) {
            return "Пришлите, пожалуйста, фото (.jpeg) и текстовый отчет (.docx)";
        } else if (!report.getPhotoPresented() && report.getTextPresented()){
            return "Осталось прислать фото";
        } else if (report.getPhotoPresented() && !report.getTextPresented()) {
            return "Осталось прислать текст";
        } else {
            return "Отчет уже получен. Можете послать еще раз";
        }
    }
}