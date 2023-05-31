package ru.netology.web.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

class MoneyTransferTest {

    @Test
    void shouldTransferMoneyFromFirstCardToSecond() {
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(firstCardBalance);
        var firstCardBalanceExpected = firstCardBalance - amount;
        var secondCardBalanceExpected = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var firstCardBalanceActual = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalanceActual = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalanceExpected, firstCardBalanceActual);
        assertEquals(secondCardBalanceExpected, secondCardBalanceActual);
    }

    @Test
    void shouldTransferMoneyFromSecondCardToFirst() {
        Configuration.holdBrowserOpen = true;
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(secondCardBalance);
        var firstCardBalanceExpected = firstCardBalance + amount;
        var secondCardBalanceExpected = secondCardBalance - amount;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCardInfo);
        var firstCardBalanceActual = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalanceActual = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalanceExpected, firstCardBalanceActual);
        assertEquals(secondCardBalanceExpected, secondCardBalanceActual);
    }

    @Test
    void shouldGetErrorNotificationIfVerificationCodeIsInvalid() {
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var VerificationCode = DataHelper.getInvalidVerificationCode(authInfo);
        verificationPage.invalidVerify(VerificationCode);
        verificationPage.findErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }
}