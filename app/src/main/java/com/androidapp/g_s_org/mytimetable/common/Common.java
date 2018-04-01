package com.androidapp.g_s_org.mytimetable.common;

import com.androidapp.g_s_org.mytimetable.container.QueryItem;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

// class for declare common valuable
public class Common {
    private Common() {
    }

    //===
    //=== strings to build an url
    //===
    // the path of API
    public static final String PATH_API = "https://api-tokyochallenge.odpt.org/api/v4/";
    // token to access API
    public static final String ACCESSTOKEN = "4440b294cf9058053daca16cd377d57877e5291ed28e5736d28c3a9641c99332";
    // kind of information to get
    public static final String QUERY_TRAIN = "odpt:Train?";
    public static final String QUERY_TRAINTIMETABLE = "odpt:TrainTimetable?";
    public static final String QUERY_STATIONORDER = "odpt:StationOrder?";
    public static final String QUERY_STATIONTIMETABLE = "odpt:StationTimetable?";
    public static final String QUERY_STATION = "odpt:Station?";
    public static final String QUERY_RAILWAY = "odpt:Railway?";
    // key to filter the response
    public static final String KEY_CALENDAR = "odpt:calendar";
    public static final String KEY_DIRECTION = "odpt:railDirection";
    public static final String KEY_DELAY = "odpt:delay";
    public static final String KEY_DEPARTURESTATION = "odpt:departureStation";
    public static final String KEY_DEPARTURETIME = "odpt:departureTime";
    public static final String KEY_DESTINATIONSTATION = "odpt:destinationStation";
    public static final String KEY_FROMSTATION = "odpt:fromStation";
    public static final String KEY_OPERATOR = "odpt:operator";
    public static final String KEY_RAILWAY = "odpt:railway";
    public static final String KEY_SAMEAS = "owl:sameAs";
    public static final String KEY_STARTSTATION = "odpt:startingStation";
    public static final String KEY_STATION = "odpt:station";
    public static final String KEY_STATIONORDER = "odpt:stationOrder";
    public static final String KEY_STATIONTIMETABLE = "odpt:stationTimetableObject";
    public static final String KEY_STATIONTITLE = "odpt:stationTitle";
    public static final String KEY_TERMINALSTATION = "odpt:terminalStation";
    public static final String KEY_TERMINALSTATIONTITLE = "odpt:terminalStationTitle";
    public static final String KEY_TITLE = "dc:title";
    public static final String KEY_TIMETABLE = "odpt:trainTimetableObject";
    public static final String KEY_TOKEN = "acl:consumerKey";
    public static final String KEY_TOSTATION = "odpt:toStation";
    public static final String KEY_TRAIN = "odpt:train";
    public static final String KEY_TRAINTYPE = "odpt:trainType";
    public static final String KEY_TRAINTYPETITLE = "odpt:trainTypeTitle";
    public static final String KEY_TRAINNUMBER = "odpt:trainNumber";
    // value to filter the response
    public static final String VAL_STATION = "odpt.Station";
    public static final String VAL_DIRECTION = "odpt.RailDirection";
    public static final String VAL_CALENDAR = "odpt.Calendar";
    public static final String VAL_STATIONTIMETABLE = "odpt.stationTimetableObject";
    public static final String VAL_DEPARTURETIME = "odpt.departureTime";
    public static final String VAL_DESTINATION = "odpt.destinationStation";
    public static final String VAL_WEEKDAY = "odpt.Calendar:Weekday";
    public static final String VAL_SATURDAY = "odpt.Calendar:Saturday";
    public static final String VAL_HOLIDAY = "odpt.Calendar:Holiday";
    public static final String VAL_SATHOLIDAY = "odpt.Calendar:SaturdayHoliday";
    //===
    //=== number of trains to display
    //===
    public static final int TRAINSNUM_DISPLAY = 3;
    //===
    //=== type of timetable
    //===
    // not provided
    public static final int NONE = 0;
    // provided dynamically (from train timetable and delay)
    public static final int REALTIME = 1;
    // provided statically (from station timetable)
    public static final int STATIC = 2;
    //===
    //=== phase of interection on making station
    //===
    public static final int SELECT_OPERATOR = 1;
    public static final int SELECT_LINE = 2;
    public static final int SELECT_STATION = 3;
    public static final int SELECT_DIRECTION = 4;
    //===
    //=== name of arguments
    //===
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_POSITION = "position";
    public static final String ARG_ORIGINAL_NAME = "original_name";
    //===
    //=== string called when DB is re-defined
    //===
    public static final String LOG_DBUPGRADE = "StationAccessHelper.onUpgrade is called";
    //===
    //=== distinguish saturday from holiday)
    //===
    public static final int NODISTINGUISH_SATURDAY = 1;
    public static final int DISTINGUISH_SATURDAY = 2;
    public static final int DISTINGUISH_INOTHERWAYS = 3;
    //===
    //=== opertor name(temporary)
    //===
    public static final String TOKYOMETRO = "odpt.Operator:TokyoMetro";
    public static final String JREAST = "odpt.Operator:East";
    public static final String KEIKYU = "odpt.Operator:Keikyu";
    public static final String KEIO = "odpt.Operator:Keio";
    public static final String TOBU = "odpt.Operator:Tobu";
    public static final String TOEI = "odpt.Operator:Toei";
    public static final String RINKAI = "odpt.Operator:TWR";

    //===
    //=== operator
    //===
    public enum Operator {
        TokyoMetro("東京メトロ", "odpt.Operator:TokyoMetro", REALTIME, DISTINGUISH_SATURDAY),
        JREast("JR東日本", "odpt.Operator:JR-East", REALTIME, NODISTINGUISH_SATURDAY),
        Keikyu("京急電鉄", "odpt.Operator:Keikyu", STATIC, DISTINGUISH_SATURDAY),
        Keio("京王電鉄", "odpt.Operator:Keio", STATIC, NODISTINGUISH_SATURDAY),
        Keisei("京成電鉄", "odpt.Operator:Keisei", NONE, DISTINGUISH_INOTHERWAYS),
        Odakyu("小田急電鉄", "odpt.Operator:Odakyu", NONE, DISTINGUISH_INOTHERWAYS),
        Seibu("西武鉄道", "odpt.Operator:Seibu", NONE, DISTINGUISH_INOTHERWAYS),
        Tobu("東武鉄道", "odpt.Operator:Tobu", STATIC, DISTINGUISH_SATURDAY),
        Toei("都営", "odpt.Operator:Toei", STATIC, NODISTINGUISH_SATURDAY),
        Tokyu("東急電鉄", "odpt.Operator:Tokyu", NONE, DISTINGUISH_INOTHERWAYS),
        Rinkai("東京臨海高速鉄道", "odpt.Operator:TWR", STATIC, DISTINGUISH_SATURDAY),
        Yurikamome("ゆりかもめ", "odpt.Operator:Yurikamome", NONE, DISTINGUISH_INOTHERWAYS);

        // name of operator
        private String mName;
        // name of operator used to send request to API
        private String mNameForQuery;
        // how to get timetable
        private int mTypeOfTimetable;
        // distinguish Saturday from Holiday
        private int mSaturdayAndHoliday;

        private Operator(String name, String nameForQuery, int typeOfTimetable, int saturdayAndHoliday) {
            mName = name;
            mNameForQuery = nameForQuery;
            mTypeOfTimetable = typeOfTimetable;
            mSaturdayAndHoliday = saturdayAndHoliday;
        }

        public String getName() {
            return mName;
        }

        public String getNameForQuery() {
            return mNameForQuery;
        }

        public int getTypeOfTimetable() {
            return mTypeOfTimetable;
        }

        public int getSaturdayAndHoliday() {
            return mSaturdayAndHoliday;
        }
    }

    //===
    //=== line (exceptionally used)
    //===
    public static final String YAMANOTE_LINE = "odpt.Railway:JR-East.Yamanote";
    //===
    //=== direction
    //===
    // directions of JR line(cannot be got from stationTimeTable)
    public static final QueryItem[] direction_JRlinear = {
            new QueryItem("上り", "odpt.RailDirection:Inbound"),
            new QueryItem("下り", "odpt.RailDirection:Outbound"),
    };
    public static final QueryItem[] direction_JRcircular = {
            new QueryItem("内回り", "odpt.RailDirection:InnerLoop"),
            new QueryItem("外回り", "odpt.RailDirection:OuterLoop")
    };
    public static final QueryItem[] direction_linear = {
            new QueryItem("上り", "odpt.RailwayDirection:Inbound"),
            new QueryItem("上り", "odpt.RailDirection:Inbound"),
            new QueryItem("下り", "odpt.RailwayDirection:Outbound"),
            new QueryItem("下り", "odpt.RailDirection:Outbound")
    };
    public static final QueryItem[] direction_circular = {
            new QueryItem("内回り", "odpt.RailwayDirection:InnerLoop"),
            new QueryItem("内回り", "odpt.RailDirection:InnerLoop"),
            new QueryItem("外回り", "odpt.RailwayDirection:OuterLoop"),
            new QueryItem("外回り", "odpt.RailDirection:OuterLoop")
    };
    //===
    //=== type of train
    //===
    public enum TrainType {
        Local("Local") {
            @Override
            public String typeOf(Operator op) {
                return "各駅停車";
            }
        },
        Express("Express") {
            @Override
            public String typeOf(Operator op) {
                return "急行";
            }
        },
        SectionExpress("SectionExpress") {
            @Override
            public String typeOf(Operator op) {
                return "区間急行";
            }
        },
        SemiExpress("SemiExpress") {
            @Override
            public String typeOf(Operator op) {
                switch (op) {
                    case Keio:
                        return "区間急行";
                    default:
                        return "準急";
                }
            }
        },
        SectionSemiExpress("SectionSemiExpress") {
            @Override
            public String typeOf(Operator op) {
                return "区間準急";
            }
        },
        AirportExpress("AirportExpress") {
            @Override
            public String typeOf(Operator op) {
                return "エア急行";
            }
        },
        LimitedExpress("LimitedExpress") {
            @Override
            public String typeOf(Operator op) {
                return "特急";
            }
        },
        SemiLimitedExpress("SemiLimitedExpress") {
            @Override
            public String typeOf(Operator op) {
                return "準特急";
            }
        },
        RapidExpress("RapidExpress") {
            @Override
            public String typeOf(Operator op) {
                return "快速急行";
            }
        },
        RapidLimitedExpress("RapidLimitedExpress") {
            @Override
            public String typeOf(Operator op) {
                return "快特";
            }
        },
        CommuterLimitedExpress("CommuterLimitedExpress") {
            @Override
            public String typeOf(Operator op) {
                return "通勤特急";
            }
        },
        AccessLimitedExpress("AccessLimitedExpress") {
            @Override
            public String typeOf(Operator op) {
                return "アクセス特急";
            }
        },
        AirportRapidLimitedExpress("AirportRapidLimitedExpress") {
            @Override
            public String typeOf(Operator op) {
                return "エア快特";
            }
        },
        Rapid("Rapid") {
            @Override
            public String typeOf(Operator op) {
                return "快速";
            }
        },
        ChuoLimitedRapid("ChuoLimitedRapid") {
            @Override
            public String typeOf(Operator op) {
                return "中央特快";
            }
        },
        CommuterRapid("CommuterRapid") {
            @Override
            public String typeOf(Operator op) {
                return "通勤快速";
            }
        },
        CommuterLimitedRapid("CommuterLimitedRapid") {
            @Override
            public String typeOf(Operator op) {
                return "通勤特快";
            }
        },
        MorningWing("MorningWing") {
            @Override
            public String typeOf(Operator op) {
                return "モーニング";
            }
        },
        FLiner("F-Liner") {
            @Override
            public String typeOf(Operator op) {
                return "Fライナー";
            }
        },
        TjLiner("TJ-Liner") {
            @Override
            public String typeOf(Operator op) {
                return "TJライナー";
            }
        },;

        // constructor
        private TrainType(String name) {
            mTypeName = name;
        }

        // field
        private final String mTypeName;

        // method
        public abstract String typeOf(Operator op);
        public static TrainType getTrainType(String type){
            for (TrainType v : values()){
                if (v.mTypeName.equals(type)){
                    return v;
                }
            }
            return null;
        }
    }

    //===
    //=== suffix of line
    //===
    public static final String SUFFIX_LINE = "ライン";
    public static final String SUFFIX_SEN = "線";
    public static final String SUFFIX_DIRECTION = "方面";
    public static final String SUFFIX_ROTATEDIRECTION = "回り";

    //===
    //=== Japanese National Holiday
    //===
    public enum JapaneseNationalHoliday {
        NewYearsDay("元日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 1, 1);
            }
        },
        ComingOfAgeDay("成人の日") {
            @Override
            public Calendar dateOf(int year) {

                return mondayOf(year, 1, 2);
            }
        },
        NationalFoundationDay("建国記念日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 2, 11);
            }
        },
        VernalEquinoxDay("春分の日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 3, calcVernalEquinoxDay(year));
            }
        },
        GreeneryDay("みどりの日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 5, 4);
            }
        },
        ShowaDay("昭和の日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 4, 29);
            }
        },
        ConstitutionMemorialDay("憲法記念日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 5, 3);
            }
        },
        ChildrensDay("こどもの日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 5, 5);
            }
        },
        MarineDay("海の日") {
            @Override
            public Calendar dateOf(int year) {
                return mondayOf(year, 7, 3);
            }
        },
        MountainDay("山の日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 8, 11);
            }
        },
        RespectForTheAgedDay("敬老の日") {
            @Override
            public Calendar dateOf(int year) {
                return mondayOf(year, 9, 3);
            }
        },
        AutumnalEquinoxDay("秋分の日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 9, calcAutumnalEquinoxDay(year));
            }
        },
        HealthAndSportsDay("体育の日") {
            @Override
            public Calendar dateOf(int year) {
                return mondayOf(year, 10, 2);
            }
        },
        NationalCultureDay("文化の日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 11, 3);
            }
        },
        LaborThanksgivingDay("勤労感謝の日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 11, 23);
            }
        },
        EmperorsBirthday("天皇誕生日") {
            @Override
            public Calendar dateOf(int year) {
                return toCalendar(year, 12, 23);
            }
        },;
        private static final double DIFF_DAY_OF_YEAR = 0.242194;
        private final String mName;

        // constructor
        private JapaneseNationalHoliday(String name) {
            mName = name;
        }

        // return the date of national holiday in the given year
        public abstract Calendar dateOf(int year);

        @Override
        public String toString() {
            return mName;
        }

        // calculate VernalEquinoxDay of the given year
        private static int calcVernalEquinoxDay(int year) {
            int diff1 = year - 1980;
            int diff2 = 0;
            double standard = 0;
            if (year <= 1979) {
                standard = 20.8357;
                diff2 = year - 1983;
            } else if (year <= 2099) {
                standard = 20.8431;
                diff2 = year - 1980;
            } else if (year <= 2150) {
                standard = 21.8510;
                diff2 = year - 1980;
            } else {
                throw new IllegalArgumentException(year + "th year is illegal value.");
            }
            return (int) (standard + DIFF_DAY_OF_YEAR * diff1 - (int) (diff2 / 4));
        }

        // calculate AutumnalEquinoxDay of the given year
        private static int calcAutumnalEquinoxDay(int year) {
            int diff1 = year - 1980;
            int diff2 = 0;
            double standard = 0;
            if (year <= 1979) {
                standard = 23.2588;
                diff2 = year - 1983;
            } else if (year <= 2099) {
                standard = 23.2488;
                diff2 = year - 1980;
            } else if (year <= 2150) {
                standard = 24.2488;
                diff2 = year - 1980;
            } else {
                throw new IllegalArgumentException(year + "th year is illegal value.");
            }
            return (int) (standard + DIFF_DAY_OF_YEAR * diff1 - (int) (diff2 / 4));
        }

        // return Calendar object
        private static Calendar toCalendar(int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, day);
            return cal;
        }

        private static Calendar mondayOf(int year, int month, int ordinal) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, ordinal);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            return cal;
        }
    }


}
