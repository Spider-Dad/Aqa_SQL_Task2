package ru.netology.test;

import org.junit.jupiter.api.Test;
import ru.netology.data.APIHelper;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITest {

    @Test
    public void validTransferFromFirstCardToSecondCardTest() {
        var authinfo = DataHelper.getAuthInfoWithTestData();

        APIHelper.makeQueryToLogin(authinfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authinfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);

        var cardsBalances = APIHelper.sendQueryToGetCardBalance(tokenInfo.getToken(), 200);
        var fromCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var toCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());

        var transferAmount = DataHelper.generateValidAmount(fromCardBalance);
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getFirstCardInfo().getNumber(), DataHelper.getSecondCardInfo().getNumber(), transferAmount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);

        cardsBalances = APIHelper.sendQueryToGetCardBalance(tokenInfo.getToken(), 200);
        var actualFromCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualToCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());

        assertAll(
                () -> assertEquals(fromCardBalance - transferAmount, actualFromCardBalance),
                () -> assertEquals(toCardBalance + transferAmount, actualToCardBalance)
        );

    }

    @Test
    public void transferWithNegativeBalanceTest() {
        var authInfo = DataHelper.getAuthInfoWithTestData();

        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);

        var cardsBalances = APIHelper.sendQueryToGetCardBalance(tokenInfo.getToken(), 200);
        var fromCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var toCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());

        var transferAmount = fromCardBalance + 100; // Переводим сумму, превышающую баланс на карте

        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getFirstCardInfo().getNumber(), DataHelper.getSecondCardInfo().getNumber(), transferAmount);

        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 400);

        cardsBalances = APIHelper.sendQueryToGetCardBalance(tokenInfo.getToken(), 200);
        var actualFromCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualToCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());

        assertAll(
                () -> assertEquals(fromCardBalance, actualFromCardBalance, "Баланс отправляющей карты не изменился"),
                () -> assertEquals(toCardBalance, actualToCardBalance, "Баланс получающей карты не изменился")
        );
    }


}
