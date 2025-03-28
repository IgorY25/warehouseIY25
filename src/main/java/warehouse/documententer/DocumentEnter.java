package warehouse.documententer;

import warehouse.cliententer.ClientEnter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import static warehouse.util.OperDay.opdate;

public class DocumentEnter {
    public static void addDoc(Connection connection) throws Exception {

        final String sqlInsert = """
                insert into documents
                (date,  numDoc, client_debet, client_kredit, product_ref, quantity, price)
                values (?,?,?,?,?,?,?);
                """;
        final String sqlInsertBalance = """
                insert into balance
                (date,  client_ref, product_ref, price, quantity_coming)
                values (?,?,?,?,?);
                """;
        final String sqlUpdateBalance = """
                update balance b
                set quantity_coming = b.quantity_coming+?
                where b.id=?;
                """;

        final String sqlSelectBalanceHelp = """
                select id
                from balance d
                where date=? and client_ref=? and product_ref=? and price=?;
                """;

        final String sqlSelectProd = """
                select p.id as number, p.name as name, u.name as unit
                from product p
                         inner join measure_unit u on p.unit_ref = u.id
                where p.id = ?;
                """;
        PreparedStatement statement;
        PreparedStatement statementInsDoc;
        statementInsDoc = connection.prepareStatement(sqlInsert);
        PreparedStatement statementHelp;

        Scanner scanner = new Scanner(System.in);
        int result = 0;
        boolean boolRes = false;
        String resultType;
        String resultStr = new String();
        ResultSet resultSet = null;
        System.out.println("");
        System.out.println("Ввод документа на приход товара на склад от поставщика за текущее число");

        int idClient = ClientEnter.clientQuery(connection, true,1);
        if (idClient == 0) {
            System.out.println("Ошибка ввода id поставщика");
            return;
        }

        BigDecimal money = new BigDecimal("0.00");
        BigDecimal quantity = new BigDecimal("0.000");
        BigDecimal zero = new BigDecimal("0");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(opdate);

        do {
            System.out.println("");
            System.out.println("Введите id следующей продукции (0-завершение ввода)");
            if (scanner.hasNext()) {
                int intRes = scanner.nextInt();
                System.out.println("Вы ввели id= %d".formatted(intRes));
                if (intRes == 0) {
                    break;
                }
                statementHelp = connection.prepareStatement(sqlSelectProd);
                statementHelp.setInt(1, intRes);
                try {
                    resultSet = statementHelp.executeQuery();
                    boolRes = resultSet.next();
                    if (boolRes) {
                        result = resultSet.getInt("number");
                        resultStr = resultSet.getString("name");
                        resultType = resultSet.getString("unit");
                        System.out.println("id продукции=" + result + " Наименование: " + resultStr + " Ед.изм: " + resultType);
                    } else {
                        System.out.println("Ошибка ввода id продукции");
                        continue;
                    }
                } catch (SQLException e) {
                    System.out.println("Ошибка ввода: " + e.toString());
                    continue;
                }

            }
            System.out.println("");

            System.out.println("Введите количество продукции ");
            if (scanner.hasNextBigDecimal()) {
                quantity = scanner.nextBigDecimal();
                System.out.println("Вы ввели количество " + quantity.toString());
                if (quantity.compareTo(zero) == 0) {
                    System.out.println("Ошибка ввода количества");
                    continue;
                }
            }
            System.out.println("Введите цену продукции ");
            if (scanner.hasNextBigDecimal()) {
                money = scanner.nextBigDecimal();
                System.out.println("Вы ввели цену " + money.toString());
                if (money.compareTo(zero) == 0) {
                    System.out.println("Ошибка ввода цены");
                    continue;
                }

                String ndoc = today + "/" + idClient;

                statementInsDoc.setDate(1, opdate);
                statementInsDoc.setString(2, ndoc);
                statementInsDoc.setInt(3, 1);
                statementInsDoc.setInt(4, idClient);
                statementInsDoc.setInt(5, result);
                statementInsDoc.setBigDecimal(6, quantity);
                statementInsDoc.setBigDecimal(7, money);

                statementHelp = connection.prepareStatement(sqlSelectBalanceHelp);
                statementHelp.setDate(1, opdate);
                statementHelp.setInt(2, 1);
                statementHelp.setInt(3, result);
                statementHelp.setBigDecimal(4, money);
                int idProd = result;
                System.out.println("Ищем в сальдо id продукции=" + idProd + " Наименование: " + resultStr);

                int idUpdate = 0;
                try {
                    resultSet = statementHelp.executeQuery();
                    boolRes = resultSet.next();
                    if (boolRes) {
                        idUpdate = resultSet.getInt("id");
                        System.out.println("Уже существует сальдо по этому товару с id=" + idUpdate);
                    } else {
                        System.out.println("Такого товара в сальдо еще нет");
                    }
                    result = statementInsDoc.executeUpdate();
                    System.out.println("Количество введенных документов: " + result);
                    if (boolRes) {
                        statement = connection.prepareStatement(sqlUpdateBalance);
                        statement.setBigDecimal(1, quantity);
                        statement.setInt(2, idUpdate);
                        result = statement.executeUpdate();
                        System.out.println("Количество обновленных строк в баланс: " + result);
                    } else {
                        statement = connection.prepareStatement(sqlInsertBalance);
                        statement.setDate(1, opdate);
                        statement.setInt(2, 1);
                        statement.setInt(3, idProd);
                        statement.setBigDecimal(4, money);
                        statement.setBigDecimal(5, quantity);
                        result = statement.executeUpdate();
                        System.out.println("Количество введенных строк в баланс: " + result);
                    }

                } catch (SQLException e) {
                    System.out.println("Ошибка ввода: " + e.toString());
                    connection.rollback();
                    return;
                }
            }
        }
        while (true);
        System.out.println("Окончание ввода прихода продукции");
    }
}
