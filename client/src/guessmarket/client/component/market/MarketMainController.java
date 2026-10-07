package guessmarket.client.component.market;

import guessmarket.client.component.account.AccountController;
import guessmarket.client.component.chat.ChatAreaController;
import guessmarket.client.component.events.EventsController;
import guessmarket.client.component.main.AppMainController;
import guessmarket.dto.UserDto;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

import static guessmarket.client.util.Constants.REFRESH_RATE;

public class MarketMainController {

    @FXML private Parent eventsComponent;
    @FXML private EventsController eventsComponentController;
    @FXML private Parent accountComponent;
    @FXML private AccountController accountComponentController;
    @FXML private Parent chatComponent;
    @FXML private ChatAreaController chatComponentController;

    private AppMainController appMainController;
    private MarketRefresher marketRefresher;
    private Timer timer;

    @FXML
    public void initialize() {
        eventsComponentController.setMarketMainController(this);
        accountComponentController.setMarketMainController(this);
    }

    public void setAppMainController(AppMainController appMainController) {
        this.appMainController = appMainController;
    }

    public void setActive() {
        marketRefresher = new MarketRefresher(this);
        timer = new Timer();
        timer.schedule(marketRefresher, 0, REFRESH_RATE);
        chatComponentController.startListRefresher();
    }

    public void refreshNow() {
        if (timer == null) {
            return;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                marketRefresher.refresh(true);
            }
        }, 0);
    }

    public void updateData(MarketData data) {
        UserDto currentUser = null;
        for (UserDto user : data.getUsers()) {
            if (user.getName().equalsIgnoreCase(getUserName())) {
                currentUser = user;
            }
        }
        appMainController.updateCurrentUser(currentUser);
        eventsComponentController.update(data);
        accountComponentController.update(data, currentUser);
    }

    public void showConnectionProblem(String problem) {
        appMainController.setConnectionProblem(problem);
    }

    public Integer getEventsTabSelectedEventId() {
        return eventsComponentController.getSelectedEventId();
    }

    public Integer getAccountTabSelectedEventId() {
        return accountComponentController.getSelectedEventId();
    }

    public String getUserName() {
        return appMainController.getCurrentUserName();
    }

    public void playFade(Node node, Duration duration) {
        appMainController.playFade(node, duration);
    }

    public void close() {
        if (timer != null) {
            timer.cancel();
        }
        chatComponentController.close();
    }
}
