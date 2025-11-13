import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
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

        $("[data-test-id=city] input").setValue("Казань");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(meetingDate);
        $("[data-test-id='name'] input").setValue("Иван Петров");
        $("[data-test-id='phone'] input").setValue("+79991234567");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=notification]").shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id=notification]").shouldHave(Condition.exactText("Успешно! Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldWorkWithComplexElements() {
        // Устанавливаем дату встречи через 7 дней от СЕГОДНЯ
        LocalDate meetingDate = LocalDate.now().plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedMeetingDate = meetingDate.format(formatter);

        // 1. Выбор города
        $("[data-test-id=city] input").setValue("Ка");
        $$(".menu-item__control").findBy(Condition.text("Казань")).click();

        // 2. Открытие календаря
        $("[data-test-id=date] .icon-button").click();

        // **ИСПРАВЛЕНИЕ №1: Правильный расчет разницы месяцев**
        // Дата, которая открывается в календаре по умолчанию (сегодня + 3 дня)
        LocalDate defaultDate = LocalDate.now().plusDays(3);
        // Вычисляем, сколько раз нужно нажать на стрелку "вперед"
        long monthsToClick = ChronoUnit.MONTHS.between(defaultDate.withDayOfMonth(1), meetingDate.withDayOfMonth(1));

        for (int i = 0; i < monthsToClick; i++) {
            $("[data-direction=next]").click();
        }

        // **ИСПРАВЛЕНИЕ №2: Правильный селектор для выбора дня**
        $$("td.calendar__day").findBy(Condition.text(String.valueOf(meetingDate.getDayOfMonth()))).click();


        // 3. Заполнение остальных полей
        $("[data-test-id='name'] input").setValue("Иван Петров-Иванов");
        $("[data-test-id='phone'] input").setValue("+79123456789");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        // 4. Проверка результата
        $("[data-test-id=notification]").shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id=notification]").shouldHave(Condition.exactText("Успешно! Встреча успешно забронирована на " + formattedMeetingDate));
    }
}
