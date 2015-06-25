package eu.unibox.handheld.testfx;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.loadui.testfx.GuiTest.find;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.GeneralMatchers.baseMatcher;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.testfx.api.annotation.Unstable;
import org.testfx.framework.junit.ApplicationTest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import eu.unibox.handheld.MainFx;

public class HandScannerTestTestFX extends ApplicationTest {
    private Stage stage;
    private String host;
    private final long id = System.currentTimeMillis();
    private String fingerprint;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        host = System.getProperty("host", "localhost");
        Window owner = primaryStage.getOwner();
        stage = new Stage();
        stage.initOwner(owner);

        new MainFx().start(stage);
        /*if (primaryStage.getStyle().equals(StageStyle.UNDECORATED)) {
            this.stage = primaryStage;
            new MainFx().start(primaryStage);
        }*/
    }

    public void stop() {
        stage.close();
    }

    @Test
    public void baptiseDevice() {
        clickOn("#modeButton");
        sleep(1000);
        verifyThat("#deviceIdText", isVisible());

        /*
        Tab t = (Tab) find("#loginTab");
        verifyThat((Tab) find("#loginTab"), TabMatchers.isSelected());
        verifyThat((Tab) find("#deliveryTab"), TabMatchers.isUnselected());
        verifyThat((Tab) find("#baptiseTab"), TabMatchers.isUnselected());*
        */

        clickOn("#deviceIdText").write("Dev" + id);
        clickOn("#deviceNameText").write("Name" + id);

        clickOn("Baptise");

        TextArea n = (TextArea) find("#messageText");

        verifyThat(n.getText(), containsString("Baptising device Dev" + id));
        fingerprint = n.getText();
        fingerprint = fingerprint.substring(fingerprint.lastIndexOf(":") + 2);

    }

    @Test
    public void loginDeviceUser() {
        try {
            baptiseDevice();
            clickOn("#modeButton");
        } catch (AssertionError e) {
        }
        assertNotNull("Device not baptised, fingerprint is null", fingerprint);
        verifyThat("#userLoginText", isVisible());
        clickOn("#userLoginText").write("Usr" + id);
        clickOn("#userPasswordText").write("password");

        clickOn("#loginButton");
        WebResource service = Client.create().resource("http://" + host + ":8080/backend/carrier/GLS");
        System.out.println(service.path("enable-user-on-device").queryParam("fingerprint", fingerprint)
                .queryParam("userLoginName", "Usr" + id).header("user", "glsUser").header("pwd", "password").get(String.class));
        System.out.println(service.path("deviceuser").path("Usr" + id).path("activate-user").header("user", "glsUser")
                .header("pwd", "password").get(String.class));
        clickOn("#loginButton");

    }

    @Test
    public void shouldClickButton() {
        clickOn("#modeButton");
    }

    private static class TabMatchers {
        @Factory
        @Unstable(reason = "is missing apidocs")
        public static Matcher<Tab> isSelected() {
            return baseMatcher("Tab is selected", tab -> tab.isSelected());
        }

        @Factory
        @Unstable(reason = "is missing apidocs")
        public static Matcher<Tab> isUnselected() {
            return baseMatcher("Tab is selected", tab -> !tab.isSelected());
        }
    }
}
