package unittesting;

import java.lang.reflect.Method;
import com.hp.lft.report.Status;
import com.hp.lft.unittesting.UnitTestBase;
import org.junit.jupiter.api.extension.*;

public class UnitTestClassBase extends UnitTestBase implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    protected static UnitTestClassBase instance;
    protected static String className;
    protected static String testName;

    public void globalTearDown() throws Exception {
        this.classTearDown();
        getReporter().generateReport();
    }

    @Override
    protected String getTestName() {
        return this.testName;
    }

    @Override
    protected String getClassName() {
        return className;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        className = extensionContext.getRequiredTestClass().getName();
        this.classSetup();

        if (instance != null)
            instance.innerClassSetup();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        globalTearDown();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        if(extensionContext.getTestMethod().get().getParameterCount() > 0){
            String testCaseName = extensionContext.getDisplayName();
            String testName = ((Method) extensionContext.getElement().get()).getName();
            parameterizedTestSetup(testName, testCaseName);
        }
        else{
            this.testName = extensionContext.getDisplayName();
            testSetup();
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        boolean isFailed = extensionContext.getExecutionException().isPresent() ? true : false;

        if(isFailed){
            setStatus(Status.Failed);
            testTearDownNoThrow();
        }else{
            setStatus(Status.Passed);
            testTearDownNoThrow();
        }
    }
}