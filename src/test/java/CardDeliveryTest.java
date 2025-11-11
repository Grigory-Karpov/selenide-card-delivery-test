import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {
    public String generateDate(long addDays, String pattern) {
        return LocalDate.now().plusDays(addDays).format(DateTimeFormatter.ofPattern(pattern));
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999/");
    }

    @Test
    void shouldSubmitRequestSuccessfully() {
        String meetingDate = generateDate(3, "dd.MM.yyyy");

        $("[data-qa-id=city] input").setValue("Казань");
        $("[data-qa-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-qa-id='date'] input").setValue(meetingDate);
        $("[data-qa-id='name'] input").setValue("Иван Петров");
        $("[data-qa-id='phone'] input").setValue("+79991234567");
        $("[data-qa-id=agreement]").click();
        $(".button").click();

        $("[data-qa-id=notification]").shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-qa-id=notification]").shouldHave(Condition.exactText("Успешно! Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldWorkWithComplexElements() {
        LocalDate today = LocalDate.now();
        LocalDate meetingDate = today.plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedMeetingDate = meetingDate.format(formatter);

        $("[data-qa-id=city] input").setValue("Ка");
        $$(".menu-item__control").findBy(Condition.text("Казань")).click();

       
        $("[data-qa-id=date] .icon-button").click();

        long monthsToClick = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), meetingDate.withDayOfMonth(1));
        
        for (int i = 0; i < monthsToClick; i++) {
            $("[data-qa-id='data-direction-next']").click();
        }

        $$(".calendar__day:not(.calendar__day_other-month)").findBy(Condition.text(String.valueOf(meetingDate.getDayOfMonth()))).click();

        $("[data-qa-id='name'] input").setValue("Иван Петров-Иванов");
        $("[data-qa-id='phone'] input").setValue("+79123456789");
        $("[data-qa-id=agreement]").click();
        $(".button").click();

        $("[data-qa-id=notification]").shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-qa-id=notification]").shouldHave(Condition.exactText("Успешно! Встреча успешно забронирована на " + formattedMeetingDate));
    }
}
