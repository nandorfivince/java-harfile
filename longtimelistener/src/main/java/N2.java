import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

public class N2 {

    /*
    Add BrowserMobProxy dependency in pom.xml
    Start the proxy on some port
    Set SSL and HTTP proxy in SeleniumPRoxy
    Add Capability for PROXY in DesiredCapabilities
    Set captureTypes
    setHarCaptureTypes with above captureTypes
    HAR name
    Start browser and open url
    get the HAR
    Write that in a file
    Preview HAR file using HTTP Archive Viewer
     */

    public static void main(String[] args) throws InterruptedException, IOException {
        BrowserMobProxy myProxy = new BrowserMobProxyServer();
        myProxy.start(0);
        Proxy seleniumProxy = new Proxy();
        seleniumProxy.setHttpProxy("localhost:"+myProxy.getPort());
        seleniumProxy.setSslProxy("localhost:"+myProxy.getPort());
        DesiredCapabilities capability = new DesiredCapabilities();
        capability.setCapability(CapabilityType.PROXY, seleniumProxy);
        capability.acceptInsecureCerts();
        capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        EnumSet <CaptureType> captureTypes = CaptureType.getAllContentCaptureTypes();
        captureTypes.addAll(CaptureType.getCookieCaptureTypes());
        captureTypes.addAll(CaptureType.getHeaderCaptureTypes());
        captureTypes.addAll(CaptureType.getRequestCaptureTypes());
        captureTypes.addAll(CaptureType.getResponseCaptureTypes());
        myProxy.setHarCaptureTypes(captureTypes);
        myProxy.newHar("MyHAR");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.merge(capability);
        WebDriver driver = new ChromeDriver(options);
        System.out.println("Driver capabilities ===>  \n"+((RemoteWebDriver)driver).getCapabilities().asMap().toString());
        driver.get("https://www.google.com");
        Thread.sleep(5000);
        Har har = myProxy.getHar();
        File myHARFile = new File(System.getProperty("user.dir")+"/HARFolder/googleHAR1.har");
        har.writeTo(myHARFile);
        System.out.println("==> HAR details has been successfully written into the file......");
        driver.close();

    }

}
