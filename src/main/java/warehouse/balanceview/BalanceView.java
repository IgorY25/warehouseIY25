package warehouse.balanceview;

import warehouse.cliententer.ClientEnter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BalanceView {
    public static int balView(Connection connection, boolean warehouse, int idClient) throws Exception {
        final String sqlSelect = """
                select d.id,d.date,tc.name vid, c.name name,p.name prod, d.price,coalesce(d.quantity_begin,0)+coalesce(d.quantity_coming,0)-coalesce(d.quantity_expense,0) as quantity_end,m.name unit,d.quantity_begin,d.quantity_coming,d.quantity_expense
                from balance d
                inner join clients c on c.id = d.client_ref
                inner join type_client tc on c.type_client_ref = tc.id
                inner join product p on p.id = d.product_ref
                inner join measure_unit m on m.id=p.unit_ref
                inner join oper_day od on d.date = od.date and od.id=1
                where d.client_ref=?;
                """;
        if (warehouse) {
            idClient = 1;
        } else {
            if (idClient == 0) {
                idClient = ClientEnter.clientQuery(connection, false, 1);
            }
        }
        PreparedStatement statement;
        ResultSet resultSet = null;
        statement = connection.prepareStatement(sqlSelect);
        statement.setInt(1, idClient);
        resultSet = statement.executeQuery();
        System.out.println("");
        System.out.println("Просмотр остатков на складе за текущее число по id=" + idClient);
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
        }
        return idClient;
    }
}
