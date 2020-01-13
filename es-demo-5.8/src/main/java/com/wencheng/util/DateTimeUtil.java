package com.wencheng.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author : 唐逸
 * @version : created date: 2019/12/18 9:30
 */
public class DateTimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeUtil.class);

    /**
     * @author : 唐逸
     * @description : 将一个日期对象格式化为一个字符串。格式为：yyyy-MM-dd HH:mm:ss
     * @date : 2019/12/18
     * @param date
     * @return java.lang.String
     */
    public static String parseDateToString(Date date) {
        if (null == date) {
            return "-1";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }


    /**
     * @author : 唐逸
     * @description : 将一个日期对象格式化为一个字符串。字符串格式由调用方法指定
     * @date : 2019/12/18
     * @param date
     * @param pattern
     * @return java.lang.String
     */
    public static String parseDateToString(Date date, String pattern) {
        if (null == date) {
            return "-1";
        }
        if (StringUtils.isEmpty(pattern)) {
            return parseDateToString(date);
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * @author : 唐逸
     * @description : 将一个字符串解析为一个日期对象。字符串格式为：yyyy-MM-dd
     * @date : 2019/12/18
     * @param dateStr
     * @return java.util.Date
     */
    public static Date parseStringToDate(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("日期转换异常");
        }
        return date;
    }

    /**
     * @author : 唐逸
     * @description : 将一个字符串解析为一个日期对象。字符串格式由调用方法指定
     * @date : 2019/12/18
     * @param dateStr
     * @param pattern
     * @return java.util.Date
     */
    public static Date parseStringToDate(String dateStr, String pattern) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            return parseStringToDate(dateStr);
        }
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("日期转换异常");
        }
        return date;
    }

    /**
     * @author : 唐逸
     * @description : 取指定日期的下一天
     * @date : 2019/12/18
     * @param dateStr
     * @return java.lang.String
     */
    public static String getNextDay(String dateStr) {
        return getBeforeOrNext(dateStr, 1);
    }

    /**
     * @author : 唐逸
     * @description : 取指定日期的前一天
     * @date : 2019/12/18
     * @param dateStr
     * @return java.lang.String
     */
    public static String getBeforeDay(String dateStr) {
        return getBeforeOrNext(dateStr, 0);
    }

     /**
     * @author : 唐逸
     * @description : 根据生日计算年龄（到当前日期）
     * @date : 2019/12/18
     * @param birthday
     * @return int
     */
    public static int getAge(Date birthday) {
        if (null == birthday) {
            return -1;
        }
        Calendar calendar = Calendar.getInstance();
        return CalculateTheAge(calendar, birthday);
    }

    /**
     * @author : 唐逸
     * @description : 根据生日计算年龄（到指定日期）
     * @date : 2019/12/18
     * @param birthday
     * @param date
     * @return int
     */
    public static int getAge(Date birthday, Date date) {
        if (null == birthday || null == date) {
            return -1;
        }
        // 获得指定时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return CalculateTheAge(calendar, birthday);
    }

    /**
     * @author : 唐逸
     * @description : 判断指定字符串是否符合日期的格式
     * @date : 2019/12/18
     * @param dateStr
     * @param pattern
     * @return boolean
     *      DateTimeUtil.isDateStr("1990-01-32 00:00:00") = false
     *      DateTimeUtil.isDateStr("1990-01 00:00") = false
     *      DateTimeUtil.isDateStr("1990-1-1 00:00:00") = true
     *      DateTimeUtil.isDateStr("1990-01-3") = true
     *      DateTimeUtil.isDateStr("1990-21-31 00:00:00") = false
     *      DateTimeUtil.isDateStr("1990年01月3日 00时00分00秒") = false
     */
    public static boolean isDateStr(String dateStr,String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        try {
            format.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * @author : 唐逸
     * @description : 计算年龄
     * @date : 2019/12/18
     * @param calendar 指定的时间
     * @param birthday 出生日期
     * @return int
     */
    private static int CalculateTheAge(Calendar calendar , Date birthday){
        if (calendar.before(birthday)) {
            // 生日在当前日期的后面
            return -1;
        }
        //获取指定的时间
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 获得生日时间
        calendar.setTime(birthday);
        int birthdayYear = calendar.get(Calendar.YEAR);
        int birthdayMonth = calendar.get(Calendar.MONTH) + 1;
        int birthdayDay = calendar.get(Calendar.DAY_OF_MONTH);
        int age = year - birthdayYear;
        if (month < birthdayMonth) {
            age--;
        }
        if (month == birthdayMonth && day<birthdayDay) {
            age--;
        }
        return age;
    }

    /**
     * @author : 唐逸
     * @description : 获取前一天或后一天
     * @date : 2019/12/18
     * @param dateStr
     * @param mark
     * @return java.lang.String
     */
    private static String getBeforeOrNext(String dateStr, int mark){
        if (StringUtils.isEmpty(dateStr)) {
            return "-1";
        }
        if (!isDateStr(dateStr,"yyyy-MM-dd")) {
            return "-1";
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(mark == 0){//前一天
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        }else if (mark == 1){//后一天
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        }else{
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    public static void main(String[] args) {


    }
}
