package guessmarket.client.util;

import com.google.gson.Gson;

public abstract class Constants {

    public final static int REFRESH_RATE = 1000;

    public final static String APP_MAIN_FXML_RESOURCE_LOCATION = "/guessmarket/client/component/main/app-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/guessmarket/client/component/login/login.fxml";
    public final static String MARKET_PAGE_FXML_RESOURCE_LOCATION = "/guessmarket/client/component/market/market-main.fxml";
    public final static String DARK_SKIN_CSS_LOCATION = "/guessmarket/client/component/main/skin-dark.css";
    public final static String SUNSET_SKIN_CSS_LOCATION = "/guessmarket/client/component/main/skin-sunset.css";

    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/guessmarket";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN = FULL_SERVER_PATH + "/login";
    public final static String UPLOAD_FILE = FULL_SERVER_PATH + "/upload-file";
    public final static String VERSION = FULL_SERVER_PATH + "/version";
    public final static String EVENTS_LIST = FULL_SERVER_PATH + "/events";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/users";
    public final static String ACCOUNT_LINES = FULL_SERVER_PATH + "/account";
    public final static String PARTICIPATION = FULL_SERVER_PATH + "/participation";
    public final static String LMSR_STATE = FULL_SERVER_PATH + "/event/lmsr-state";
    public final static String ORDER_BOOK_STATE = FULL_SERVER_PATH + "/event/order-book-state";
    public final static String OPEN_EVENT = FULL_SERVER_PATH + "/event/open";
    public final static String CLOSE_EVENT = FULL_SERVER_PATH + "/event/close";
    public final static String LOAD_FUNDS = FULL_SERVER_PATH + "/load-funds";
    public final static String BUY_SHARES = FULL_SERVER_PATH + "/trade/buy";
    public final static String SUBMIT_ORDER = FULL_SERVER_PATH + "/trade/order";
    public final static String SEND_CHAT_LINE = FULL_SERVER_PATH + "/chat/send";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";

    public final static String LMSR = "LMSR";
    public final static String ORDER_BOOK = "Order Book";
    public final static String NOT_STARTED = "Not started";
    public final static String ACTIVE = "Active";

    public final static Gson GSON_INSTANCE = new Gson();
}
