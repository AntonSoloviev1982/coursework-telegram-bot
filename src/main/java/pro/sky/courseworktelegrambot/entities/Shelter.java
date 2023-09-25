package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

/**
 * Класс Shelter. Описывает объект приюта
 * Имеет свойства <b>id</b>, <b>name</b> , <b>information</b> , <b>timetable</b>,
 * <b>address</b> , <b>security</b>, <b>safetyPrecautions</b>, <b>rules</b>,
 * <b>documents</b>, <b>transportation</b>, <b>childAccomodation</b>,
 * <b>adultAccomodation</b>, <b>invalidAccomodation</b>, <b>communication</b>, <b>cynologists</b>,
 * <b>refusalReasons</b>,
 */

@Entity
@Table(name = "shelter")
public class Shelter {
    /**
     * Уникальное значение, которое присваивается при создании нового приюта.
     */
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)

    private String id;
    /**
     * Название приюта
     */
    private String name;
    /**
     * Краткая информация о приюте
     */
    private String information;
    /**
     * график работы
     */
    private String timetable;
    /**
     * адресс приюта
     */
    private String address;
    /**
     * безопасность
     */
    private String security;
    /**
     * Техника безопасности
     */
    private String safetyPrecautions;

    /**
     * Правила
     */
    private String rules;
    /**
     * Документы приюта
     */
    private String documents;
    /**
     * Услуги по перевозки
     */
    private String transportation;
    /**
     * отделение молодых питомцев
     */
    private String childAccomodation;
    /**
     * отделение взрослых питомцев
     */
    private String adultAccomodation;
    /**
     * отделение питомцев с инвалидностью
     */
    private String invalidAccomodation;
    /**
     * как связаться с приютом
     */
    private String communication;
    /**
     * ответственное лицо за воспитание и уход питомца
     */
    private String cynologists;
    /**
     * причина отказа
     */
    private String refusalReasons;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getTimetable() {
        return timetable;
    }

    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getSafetyPrecautions() {
        return safetyPrecautions;
    }

    public void setSafetyPrecautions(String safetyPrecautions) {
        this.safetyPrecautions = safetyPrecautions;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public String getChildAccomodation() {
        return childAccomodation;
    }

    public void setChildAccomodation(String childAccomodation) {
        this.childAccomodation = childAccomodation;
    }

    public String getAdultAccomodation() {
        return adultAccomodation;
    }

    public void setAdultAccomodation(String adultAccomodation) {
        this.adultAccomodation = adultAccomodation;
    }

    public String getInvalidAccomodation() {
        return invalidAccomodation;
    }

    public void setInvalidAccomodation(String invalidAccomodation) {
        this.invalidAccomodation = invalidAccomodation;
    }

    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    public String getCynologists() {
        return cynologists;
    }

    public void setCynologists(String cynologists) {
        this.cynologists = cynologists;
    }

    public String getRefusalReasons() {
        return refusalReasons;
    }

    public void setRefusalReasons(String refusalReasons) {
        this.refusalReasons = refusalReasons;
    }
}
