import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryTest {


    public String generateDate(long addDays, String pattern) {
        return LocalDate.now().plusDays(addDays).format(DateTimeFormatter.ofPattern(pattern));
    }

    @BeforeEach
    void setUp() {

        open("http://localhost:9999");
    }

    @Test
    void shouldSubmitRequestSuccessfully() {

        String meetingDate = generateDate(3, "dd.MM.yyyy");


        $("[data-test-id=city] input").setValue("Казань");


        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(meetingDate);


        $("[data-test-id=name] input").setValue("Иванов Иван");


        $("[data-test-id=phone] input").setValue("+79991234567");


        $("[data-test-id=agreement]").click();


        $("button.button").click();


        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.exactText("Успешно!\nВстреча успешно забронирована на " + meetingDate));
    }
}