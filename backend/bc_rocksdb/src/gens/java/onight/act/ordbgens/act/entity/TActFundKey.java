package onight.act.ordbgens.act.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TActFundKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_ACT_FUND.FUND_NO
     *
     * @mbggenerated Tue Jan 19 22:41:13 CST 2016
     */
    private String fundNo;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_ACT_FUND.FUND_NO
     *
     * @return the value of T_ACT_FUND.FUND_NO
     *
     * @mbggenerated Tue Jan 19 22:41:13 CST 2016
     */
    public String getFundNo() {
        return fundNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_ACT_FUND.FUND_NO
     *
     * @param fundNo the value for T_ACT_FUND.FUND_NO
     *
     * @mbggenerated Tue Jan 19 22:41:13 CST 2016
     */
    public void setFundNo(String fundNo) {
        this.fundNo = fundNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_ACT_FUND
     *
     * @mbggenerated Tue Jan 19 22:41:13 CST 2016
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TActFundKey other = (TActFundKey) that;
        return (this.getFundNo() == null ? other.getFundNo() == null : this.getFundNo().equals(other.getFundNo()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_ACT_FUND
     *
     * @mbggenerated Tue Jan 19 22:41:13 CST 2016
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFundNo() == null) ? 0 : getFundNo().hashCode());
        return result;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_ACT_FUND
     *
     * @mbggenerated Tue Jan 19 22:41:13 CST 2016
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fundNo=").append(fundNo);
        sb.append("]");
        return sb.toString();
    }
}