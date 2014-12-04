package com.seg2.kcl2d.json;

import com.seg2.kcl2d.Country;

/**
 * Created by Thomas on 2/12/2014.
 */
public class IndicatorClass {

    private Indicator indicator;
    private Country country;
    private String value;
    private String decimal;
    private String date;

    /**
     * The indicator
     *
     * @return
     */
    public Indicator getIndicator() {
        return indicator;
    }

    /**
     * @param indicator The indicator
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    /**
     * @return The country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The decimal
     */
    public String getDecimal() {
        return decimal;
    }

    /**
     * @param decimal The decimal
     */
    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    /**
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    public class Indicator {

        private String id;
        private String value;

        /**
         * Getter method for the id
         *
         * @return
         */
        public String getId() {
            return id;
        }

        /**
         * Setter method for the id
         *
         * @param id New id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Getter method for the value
         *
         * @return
         */
        public String getValue() {
            return value;
        }

        /**
         * Setter method for the value
         *
         * @param value New value
         */
        public void setValue(String value) {
            this.value = value;
        }

    }
}