package com.androidapp.g_s_org.mytimetable.common;


// done
// 必要な情報を取ってきてパースするメソッドの作成
// (駅時刻表ではなく、最も近い列車を5つ取得し、列車時刻表を列番で引っ掛けないと厳しい)
// 情報を更新するメソッドの作成
// 駅のdrawableを作る
// 路線に対応した方面をAPIをたたいてGETするメソッドの作成
// 初期設定値をDBから読みだすメソッドの作成
// 日付の判定
// 初期設定するダイアログと設定値をDBに書き込むメソッドの作成(createStation)
// 駅の削除をするメソッドの作成
// 駅のコードに対応したtitleをAPIをたたいてGETするメソッドの作成(可能なら方面の日本語名もgetしたい)
// 方面とか線名が出ない場合があるので、きちんと表示させる
// API取得中に画面切り替えると落ちると思われる(cancelの実装)
// 画面名を変更するメソッドの作成
// なぜか駅が2つ消える
// 駅名の日本語化(DBの読み書きでうまくいかない)
// 2回押すと、空白のところに前の行のデータが入ってくる
// >viewholderに勝手に当てはめられるっぽいので、trainがnullのときは明示的に空文字を入れるようにする

// todo
// 在線情報を出している事業者がJRとメトロだけなので、駅時刻表から渡すのも必要
// stringリソースをまとめて、commonを整理
// 列車の種別も整理する


// レイアウトの修正
// 画面切り替え時(とonDestroy?)でmTrainsListのリフレッシュするの忘れずに！
// getでとれなかった場合に再送するメソッドの作成
// 駅の並べ替えをするメソッドの作成


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

        public int getTypeOfTimetable(){ return mTypeOfTimetable; }

        public int getSaturdayAndHoliday(){ return mSaturdayAndHoliday; }
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
    public static final Map<String, String> TrainTypeMap = new HashMap<String, String>(){
        {
            put("odpt.TrainType:Local", "普通");
            put("odpt.TrainType:Express", "急行");
            put("odpt.TrainType:Rapid", "快速");
            put("odpt.TrainType:LimitedExpress", "特急");
            put("odpt.TrainType:TokyoMetro.Local", "普通");
            put("odpt.TrainType:TokyoMetro.Rapid", "快速");
            put("odpt.TrainType:JR-East:Local", "普通");
            put("odpt.TrainType:JR-East:Rapid", "快速");
            put("odpt.TrainType:JR-East:ChuoLimitedRapid", "中央特快");
            put("odpt.TrainType:JR-East:CommuterLimitedRapid", "通勤特快");
            put("odpt.TrainType:JR-East:CommuterRapid", "通勤快速");
        }
    };
    public static final QueryItem[] trainTypeItems = {
            new QueryItem("普通", "Local"),
            new QueryItem("快速", "Rapid"),
            new QueryItem("急行", "Express"),
    };
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
        },
        ;
        private static final double DIFF_DAY_OF_YEAR        = 0.242194;
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
            return (int)(standard + DIFF_DAY_OF_YEAR * diff1 - (int)(diff2 / 4));
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
            return (int)(standard + DIFF_DAY_OF_YEAR * diff1 - (int)(diff2 / 4));
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
