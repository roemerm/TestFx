package eu.unibox.handheld.testfx;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class CaptureScreenshot extends TestWatcher {

    @Override
    public void failed(Throwable t, Description description) {
        System.out.println("TC " + description + " failed");
        System.out.println("TC " + description.getClassName() + " failed");
        System.out.println("TC " + description.getDisplayName() + " failed");
        System.out.println("TC " + description.getMethodName() + " failed");
        System.out.println("TC " + description.getTestClass() + " failed");
        String path = "." + File.separator + "build" + File.separator + "screenshots";
        File capturePath = new File(path);

        String message = t.getMessage();
        Pattern p = Pattern.compile("(screenshot-[0-9-]+\\.png)");
        Matcher m = p.matcher(message);
        if (m.find()) {
            String filename = m.group(1);
            File screenshoot = new File("." + File.separator + filename);
            if (!capturePath.exists()) {
                capturePath.mkdirs();
            }
            File target = new File(capturePath, filename);
            screenshoot.renameTo(target);
        }
    }

    @Override
    public void succeeded(Description description) {
        System.out.println("TC " + description + " successfull");
    }
}
