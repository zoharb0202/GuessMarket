package guessmarket.client.component.market;

import guessmarket.client.util.Constants;
import guessmarket.client.util.http.HttpClientUtil;
import guessmarket.dto.AccountLineDto;
import guessmarket.dto.EventDto;
import guessmarket.dto.EventStateDto;
import guessmarket.dto.OrderBookStateDto;
import guessmarket.dto.ParticipationDto;
import guessmarket.dto.UserDto;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import static guessmarket.client.util.Constants.GSON_INSTANCE;

public class MarketRefresher extends TimerTask {

    private final MarketMainController marketMainController;
    private int lastVersion = -1;
    private String lastProblem;

    public MarketRefresher(MarketMainController marketMainController) {
        this.marketMainController = marketMainController;
    }

    @Override
    public void run() {
        refresh(false);
    }

    public synchronized void refresh(boolean force) {
        try {
            int version = GSON_INSTANCE.fromJson(fetch(Constants.VERSION), Integer.class);
            if (version != lastVersion || force) {
                List<EventDto> events = Arrays.asList(GSON_INSTANCE.fromJson(fetch(Constants.EVENTS_LIST), EventDto[].class));
                List<UserDto> users = Arrays.asList(GSON_INSTANCE.fromJson(fetch(Constants.USERS_LIST), UserDto[].class));
                List<AccountLineDto> accountLines = Arrays.asList(GSON_INSTANCE.fromJson(fetch(Constants.ACCOUNT_LINES), AccountLineDto[].class));
                EventDetails eventsTabDetails = fetchDetails(marketMainController.getEventsTabSelectedEventId(), events);
                EventDetails accountTabDetails = fetchDetails(marketMainController.getAccountTabSelectedEventId(), events);

                lastVersion = version;
                MarketData data = new MarketData(events, users, accountLines, eventsTabDetails, accountTabDetails);
                Platform.runLater(() -> marketMainController.updateData(data));
            }
            reportProblem(null);
        } catch (IOException | RuntimeException e) {
            reportProblem(e.getMessage());
        }
    }

    private EventDetails fetchDetails(Integer eventId, List<EventDto> events) throws IOException {
        if (eventId == null) {
            return null;
        }
        EventDto event = null;
        for (EventDto current : events) {
            if (current.getId() == eventId) {
                event = current;
            }
        }
        if (event == null) {
            return null;
        }

        String id = String.valueOf(eventId);
        EventStateDto lmsrState = null;
        OrderBookStateDto orderBookState = null;
        if (Constants.LMSR.equals(event.getMethodType())) {
            lmsrState = GSON_INSTANCE.fromJson(fetch(withEventId(Constants.LMSR_STATE, id)), EventStateDto.class);
        } else {
            orderBookState = GSON_INSTANCE.fromJson(fetch(withEventId(Constants.ORDER_BOOK_STATE, id)), OrderBookStateDto.class);
        }
        ParticipationDto participation = GSON_INSTANCE.fromJson(fetch(withEventId(Constants.PARTICIPATION, id)), ParticipationDto.class);
        return new EventDetails(event, lmsrState, orderBookState, participation);
    }

    private String withEventId(String url, String eventId) {
        return HttpUrl.parse(url).newBuilder().addQueryParameter("eventId", eventId).build().toString();
    }

    private String fetch(String finalUrl) throws IOException {
        try (Response response = HttpClientUtil.runSync(finalUrl)) {
            String responseBody = response.body().string();
            if (response.code() != 200) {
                throw new IOException(responseBody);
            }
            return responseBody;
        }
    }

    private void reportProblem(String problem) {
        boolean changed = problem == null ? lastProblem != null : !problem.equals(lastProblem);
        if (changed) {
            lastProblem = problem;
            Platform.runLater(() -> marketMainController.showConnectionProblem(problem));
        }
    }
}
