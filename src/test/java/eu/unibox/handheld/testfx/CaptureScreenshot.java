package eu.unibox.handheld.testfx;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class CaptureScreenshot extends TestWatcher {

    @Override
    public void failed(Throwable t, Description description) {
        String path = "." + File.separator + "build" + File.separator + "screenshots";
        File capturePath = new File(path);
        if (!capturePath.exists()) {
            capturePath.mkdirs();
        }

        String message = t.getMessage();
        Pattern p = Pattern.compile("(screenshot-\\S+\\.png)");
        Matcher m = p.matcher(message);
        if (m.find()) {
            String filename = m.group(1);
            File[] screenshoots = new File("." + File.separator).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    if (name.equals(filename)) {
                        return true;
                    }
                    return false;
                }
            });
            File target = new File(path + File.separator + filename);
            screenshoots[0].renameTo(target);
        }
    }
}
