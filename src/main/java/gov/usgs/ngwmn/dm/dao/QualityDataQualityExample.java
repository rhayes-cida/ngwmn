package gov.usgs.ngwmn.dm.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QualityDataQualityExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public QualityDataQualityExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andConsituentIsNull() {
            addCriterion("CONSITUENT is null");
            return (Criteria) this;
        }

        public Criteria andConsituentIsNotNull() {
            addCriterion("CONSITUENT is not null");
            return (Criteria) this;
        }

        public Criteria andConsituentEqualTo(String value) {
            addCriterion("CONSITUENT =", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentNotEqualTo(String value) {
            addCriterion("CONSITUENT <>", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentGreaterThan(String value) {
            addCriterion("CONSITUENT >", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentGreaterThanOrEqualTo(String value) {
            addCriterion("CONSITUENT >=", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentLessThan(String value) {
            addCriterion("CONSITUENT <", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentLessThanOrEqualTo(String value) {
            addCriterion("CONSITUENT <=", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentLike(String value) {
            addCriterion("CONSITUENT like", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentNotLike(String value) {
            addCriterion("CONSITUENT not like", value, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentIn(List<String> values) {
            addCriterion("CONSITUENT in", values, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentNotIn(List<String> values) {
            addCriterion("CONSITUENT not in", values, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentBetween(String value1, String value2) {
            addCriterion("CONSITUENT between", value1, value2, "consituent");
            return (Criteria) this;
        }

        public Criteria andConsituentNotBetween(String value1, String value2) {
            addCriterion("CONSITUENT not between", value1, value2, "consituent");
            return (Criteria) this;
        }

        public Criteria andMd5IsNull() {
            addCriterion("MD5 is null");
            return (Criteria) this;
        }

        public Criteria andMd5IsNotNull() {
            addCriterion("MD5 is not null");
            return (Criteria) this;
        }

        public Criteria andMd5EqualTo(String value) {
            addCriterion("MD5 =", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5NotEqualTo(String value) {
            addCriterion("MD5 <>", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5GreaterThan(String value) {
            addCriterion("MD5 >", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5GreaterThanOrEqualTo(String value) {
            addCriterion("MD5 >=", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5LessThan(String value) {
            addCriterion("MD5 <", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5LessThanOrEqualTo(String value) {
            addCriterion("MD5 <=", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5Like(String value) {
            addCriterion("MD5 like", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5NotLike(String value) {
            addCriterion("MD5 not like", value, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5In(List<String> values) {
            addCriterion("MD5 in", values, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5NotIn(List<String> values) {
            addCriterion("MD5 not in", values, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5Between(String value1, String value2) {
            addCriterion("MD5 between", value1, value2, "md5");
            return (Criteria) this;
        }

        public Criteria andMd5NotBetween(String value1, String value2) {
            addCriterion("MD5 not between", value1, value2, "md5");
            return (Criteria) this;
        }

        public Criteria andFirstdateIsNull() {
            addCriterion("FIRSTDATE is null");
            return (Criteria) this;
        }

        public Criteria andFirstdateIsNotNull() {
            addCriterion("FIRSTDATE is not null");
            return (Criteria) this;
        }

        public Criteria andFirstdateEqualTo(Date value) {
            addCriterion("FIRSTDATE =", value, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateNotEqualTo(Date value) {
            addCriterion("FIRSTDATE <>", value, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateGreaterThan(Date value) {
            addCriterion("FIRSTDATE >", value, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateGreaterThanOrEqualTo(Date value) {
            addCriterion("FIRSTDATE >=", value, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateLessThan(Date value) {
            addCriterion("FIRSTDATE <", value, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateLessThanOrEqualTo(Date value) {
            addCriterion("FIRSTDATE <=", value, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateIn(List<Date> values) {
            addCriterion("FIRSTDATE in", values, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateNotIn(List<Date> values) {
            addCriterion("FIRSTDATE not in", values, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateBetween(Date value1, Date value2) {
            addCriterion("FIRSTDATE between", value1, value2, "firstdate");
            return (Criteria) this;
        }

        public Criteria andFirstdateNotBetween(Date value1, Date value2) {
            addCriterion("FIRSTDATE not between", value1, value2, "firstdate");
            return (Criteria) this;
        }

        public Criteria andLastdateIsNull() {
            addCriterion("LASTDATE is null");
            return (Criteria) this;
        }

        public Criteria andLastdateIsNotNull() {
            addCriterion("LASTDATE is not null");
            return (Criteria) this;
        }

        public Criteria andLastdateEqualTo(Date value) {
            addCriterion("LASTDATE =", value, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateNotEqualTo(Date value) {
            addCriterion("LASTDATE <>", value, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateGreaterThan(Date value) {
            addCriterion("LASTDATE >", value, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateGreaterThanOrEqualTo(Date value) {
            addCriterion("LASTDATE >=", value, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateLessThan(Date value) {
            addCriterion("LASTDATE <", value, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateLessThanOrEqualTo(Date value) {
            addCriterion("LASTDATE <=", value, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateIn(List<Date> values) {
            addCriterion("LASTDATE in", values, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateNotIn(List<Date> values) {
            addCriterion("LASTDATE not in", values, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateBetween(Date value1, Date value2) {
            addCriterion("LASTDATE between", value1, value2, "lastdate");
            return (Criteria) this;
        }

        public Criteria andLastdateNotBetween(Date value1, Date value2) {
            addCriterion("LASTDATE not between", value1, value2, "lastdate");
            return (Criteria) this;
        }

        public Criteria andCtIsNull() {
            addCriterion("CT is null");
            return (Criteria) this;
        }

        public Criteria andCtIsNotNull() {
            addCriterion("CT is not null");
            return (Criteria) this;
        }

        public Criteria andCtEqualTo(Integer value) {
            addCriterion("CT =", value, "ct");
            return (Criteria) this;
        }

        public Criteria andCtNotEqualTo(Integer value) {
            addCriterion("CT <>", value, "ct");
            return (Criteria) this;
        }

        public Criteria andCtGreaterThan(Integer value) {
            addCriterion("CT >", value, "ct");
            return (Criteria) this;
        }

        public Criteria andCtGreaterThanOrEqualTo(Integer value) {
            addCriterion("CT >=", value, "ct");
            return (Criteria) this;
        }

        public Criteria andCtLessThan(Integer value) {
            addCriterion("CT <", value, "ct");
            return (Criteria) this;
        }

        public Criteria andCtLessThanOrEqualTo(Integer value) {
            addCriterion("CT <=", value, "ct");
            return (Criteria) this;
        }

        public Criteria andCtIn(List<Integer> values) {
            addCriterion("CT in", values, "ct");
            return (Criteria) this;
        }

        public Criteria andCtNotIn(List<Integer> values) {
            addCriterion("CT not in", values, "ct");
            return (Criteria) this;
        }

        public Criteria andCtBetween(Integer value1, Integer value2) {
            addCriterion("CT between", value1, value2, "ct");
            return (Criteria) this;
        }

        public Criteria andCtNotBetween(Integer value1, Integer value2) {
            addCriterion("CT not between", value1, value2, "ct");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated do_not_delete_during_merge Fri May 25 11:07:09 CDT 2012
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table GW_DATA_PORTAL.QUALITY_DATA_QUALITY
     *
     * @mbggenerated Fri May 25 11:07:09 CDT 2012
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}