package warehouse.balancesheet;

import warehouse.cliententer.ClientEnter;
import warehouse.loandebitturnover.LoanDebitTurnover;
import warehouse.util.DbHelper;
import warehouse.util.OperDay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static warehouse.util.OperDay.opdate;

public class BalanceSheet {
    public static void balSheet() throws Exception {
        System.out.println("");
        try (Connection connection = DbHelper.createConnection()) {
            if (OperDay.setOpdate(connection)) {
                System.out.println("Получили опердень:" + opdate);
            } else {
                System.out.println("Ошибка получения опердня");
                return;
            }
            final String sqlSelect = """
                    select d.id,d.date,tc.name vid, c.name name, p.id idprod, p.name prod, d.price,coalesce(d.quantity_begin,0)+coalesce(d.quantity_coming,0)-coalesce(d.quantity_expense,0) as quantity_end,m.name unit,d.quantity_begin,d.quantity_coming,d.quantity_expense
                    from balance d
                    inner join clients c on c.id = d.client_ref
                    inner join type_client tc on c.type_client_ref = tc.id
                    inner join product p on p.id = d.product_ref
                    inner join measure_unit m on m.id=p.unit_ref
                    inner join oper_day od on d.date = od.date and od.id=1
                    where d.client_ref=?;
                    """;

            int idClient = ClientEnter.clientQuery(connection, false, 0);

            PreparedStatement statement;
            ResultSet resultSet = null;
            statement = connection.prepareStatement(sqlSelect);
            statement.setInt(1, idClient);
            resultSet = statement.executeQuery();
            System.out.println("");
            System.out.println("Оборотно-сальдовая ведомость за текущее число по подразделению");
            while (resultSet.next()) {
                System.out.println("id=" + resultSet.getInt("id") +
                        ".Дата " + resultSet.getDate("date") +
                        ",Вид:" + resultSet.getString("vid") +
                        ",Наим.:" + resultSet.getString("name") +
                        ",Товар:" + resultSet.getString("prod") +
                        ",цена=" + resultSet.getBigDecimal("price") + " руб" +
                        ",тек.кол-во=" + resultSet.getBigDecimal("quantity_end") +
                        " " + resultSet.getString("unit") +
                        ",кол-во на нач.дня=" + resultSet.getBigDecimal("quantity_begin") +
                        ",приход за день=" + resultSet.getBigDecimal("quantity_coming") +
                        ",расход за день=" + resultSet.getBigDecimal("quantity_expense")
                );
                LoanDebitTurnover.turnover(connection, false, idClient, resultSet.getInt("idprod"), resultSet.getBigDecimal("price"));
                LoanDebitTurnover.turnover(connection, true, idClient, resultSet.getInt("idprod"), resultSet.getBigDecimal("price"));
            }
            System.out.println("Выход");
        }
    }
}
