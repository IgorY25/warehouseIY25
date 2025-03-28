package warehouse.transferproductsinvoice;

import warehouse.balanceview.BalanceView;
import warehouse.cliententer.ClientEnter;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import static warehouse.util.OperDay.opdate;

public class TransferProductsInvoice {
    public static void transInvoice(Connection connection, boolean isWarehouse) throws Exception {

        final String sqlInsert = """
                insert into documents
                (date,  numDoc, client_debet, client_kredit, product_ref, quantity, price)
                select b.date,?,?,b.client_ref,b.product_ref,?,b.price
                from balance b where b.id=?;
                """;

        final String sqlUpdateBalance = """
                update balance b
                set quantity_expense = coalesce(b.quantity_expense,0)+?
                where b.id=?;
                """;
        final String sqlUpdateBalanceAdd = """
                update balance
                set quantity_coming = coalesce(quantity_coming,0)+?
                where id=?;
                """;

        final String sqlIdBalance = """
                select id
                from balance
                where id=?;
                """;

        final String sqlSelectBalanceHelp = """
                select d.id
                from balance d, balance b 
                where b.id=? and d.date=b.date and d.client_ref=? 
                            and d.product_ref=b.product_ref and d.price=b.price;
                """;

        final String sqlInsertBalance = """
                insert into balance
                (date,  client_ref, product_ref, price, quantity_coming)
                select b.date,?,b.product_ref,b.price,?
                from balance b where b.id=?;
                """;

        PreparedStatement statement;
        PreparedStatement statementInsDoc;
        statementInsDoc = connection.prepareStatement(sqlInsert);
        PreparedStatement statementHelp;
        statementHelp = connection.prepareStatement(sqlIdBalance);
        PreparedStatement statementHelpBal;
        Scanner scanner = new Scanner(System.in);
        int result = 0;
        boolean boolRes = false;
        String resultType;
        String resultStr = new String();
        ResultSet resultSet = null;
        System.out.println("");
        int idClient = 0;
        if (isWarehouse) {
            System.out.println("Выдача товара со склада в магазины");
        } else {
            System.out.println("Списание проданного товара с магазина");
        }

        idClient = ClientEnter.clientQuery(connection, false, 1);
        if (idClient == 0) {
            System.out.println("Ошибка ввода id магазина");
            return;
        }

        BigDecimal quantity = new BigDecimal("0.000");
        BigDecimal zero = new BigDecimal("0");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(opdate);
        int intRes = 0;
        do {
            System.out.println("");
            if (isWarehouse) {
                BalanceView.balView(connection, isWarehouse, 0);
            } else {
                BalanceView.balView(connection, isWarehouse, idClient);
            }
            System.out.println("Введите id строки с выбранным товаром (0-завершение ввода)");
            if (scanner.hasNext()) {
                intRes = scanner.nextInt();
                System.out.println("Вы ввели id= %d".formatted(intRes));
                if (intRes == 0) {
                    break;
                }

                try {
                    statementHelp.setInt(1, intRes);
                    resultSet = statementHelp.executeQuery();
                    boolRes = resultSet.next();
                    if (boolRes) {
                        System.out.println("Строка " + intRes + " существует в сальдо");
                    } else {
                        System.out.println("Такой строки в сальдо нет");
                        continue;
                    }
                } catch (SQLException e) {
                    System.out.println("Ошибка ввода: " + e.toString());
                    continue;
                }

            }

            System.out.println("Введите количество продукции ");
            if (scanner.hasNextBigDecimal()) {
                quantity = scanner.nextBigDecimal();
                System.out.println("Вы ввели количество " + quantity.toString());
                if (quantity.compareTo(zero) == 0) {
                    System.out.println("Ошибка ввода количества");
                    continue;
                }
            }
            if (isWarehouse) {
                String ndoc = today + "/" + idClient;

                statementInsDoc.setString(1, ndoc);
                statementInsDoc.setInt(2, idClient);
                statementInsDoc.setBigDecimal(3, quantity);
                statementInsDoc.setInt(4, intRes);

                try {
                    result = statementInsDoc.executeUpdate();
                    System.out.println("Количество введенных документов: " + result);

                    statement = connection.prepareStatement(sqlUpdateBalance);
                    statement.setBigDecimal(1, quantity);
                    statement.setInt(2, intRes);
                    result = statement.executeUpdate();
                    System.out.println("Количество обновленных строк в сальдо: " + result);

                    statementHelpBal = connection.prepareStatement(sqlSelectBalanceHelp);
                    statementHelpBal.setInt(1, intRes);
                    statementHelpBal.setInt(2, idClient);

                    System.out.println("Ищем в сальдо магазина продукцию");
                    int idUpdate = 0;
                    try {
                        resultSet = statementHelpBal.executeQuery();

                        boolRes = resultSet.next();
                        if (boolRes) {
                            idUpdate = resultSet.getInt("id");
                            System.out.println("Уже существует сальдо по этому товару с id=" + idUpdate);
                        } else {
                            System.out.println("Такого товара в сальдо еще нет");
                        }
                        if (boolRes) {
                            statement = connection.prepareStatement(sqlUpdateBalanceAdd);
                            statement.setBigDecimal(1, quantity);
                            statement.setInt(2, idUpdate);
                            result = statement.executeUpdate();
                            System.out.println("Количество обновленных строк в сальдо магазина: " + result);
                        } else {
                            statement = connection.prepareStatement(sqlInsertBalance);
                            statement.setInt(1, idClient);
                            statement.setBigDecimal(2, quantity);
                            statement.setInt(3, intRes);
                            result = statement.executeUpdate();
                            System.out.println("Количество введенных строк в баланс: " + result);
                        }

                    } catch (SQLException e) {
                        System.out.println("Ошибка ввода: " + e.toString());
                        connection.rollback();
                        return;
                    }

                    connection.commit();

                } catch (SQLException e) {
                    System.out.println("Ошибка ввода (проверьте есть нужное количество на складе?): " + e.toString());
                    connection.rollback();
                    return;
                }
            } else {
                String ndoc = today + "sale/" + idClient;

                statementInsDoc.setString(1, ndoc);
                statementInsDoc.setInt(2, 6);
                statementInsDoc.setBigDecimal(3, quantity);
                statementInsDoc.setInt(4, intRes);

                try {
                    result = statementInsDoc.executeUpdate();
                    System.out.println("Количество введенных документов: " + result);

                    statement = connection.prepareStatement(sqlUpdateBalance);
                    statement.setBigDecimal(1, quantity);
                    statement.setInt(2, intRes);
                    result = statement.executeUpdate();
                    System.out.println("Количество обновленных строк в сальдо: " + result);
                    connection.commit();

                } catch (SQLException e) {
                    System.out.println("Ошибка ввода (проверьте есть нужное количество на складе?): " + e.toString());
                    connection.rollback();
                    return;
                }
            }

        }
        while (true);
        System.out.println("Окончание ввода расхода продукции");
    }
}
