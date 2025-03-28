package warehouse.loandebitturnover;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoanDebitTurnover {
    public static void turnover(Connection connection, boolean isCredit, int idClient, int idProd, BigDecimal price) throws Exception {
        String sqlSelect = (isCredit)?"""
                        select 'Расход ' action,d.id,d.date,d.numDoc,t.name vidd, c.name named,t2.name vidk, c2.name namek,p.name prod,d.quantity,m.name unit,d.price
                    """:"""
                        select 'Приход ' action,d.id,d.date,d.numDoc,t.name vidd, c.name named,t2.name vidk, c2.name namek,p.name prod,d.quantity,m.name unit,d.price
                    """;
                sqlSelect += """
                from documents d
                inner join clients c on c.id = d.client_debet
                inner join type_client t on t.id=c.type_client_ref
                inner join clients c2 on c2.id = d.client_kredit
                inner join type_client t2 on t2.id=c2.type_client_ref
                inner join product p on p.id = d.product_ref and p.id=?
                inner join measure_unit m on m.id=p.unit_ref
                inner join oper_day od on d.date = od.date and od.id=1
                """;
        sqlSelect += (isCredit)?"""
                    where d.client_kredit=? and d.price=?;
                    """:"""
                    where d.client_debet=? and d.price=?;
                    """;

        PreparedStatement statement;
        ResultSet resultSet = null;
        statement = connection.prepareStatement(sqlSelect);
        statement.setInt(1, idProd);
        statement.setInt(2, idClient);
        statement.setBigDecimal(3, price);
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            if (!isCredit) {
                System.out.println(resultSet.getString("action") +
                        " N документа " + resultSet.getString("numDoc") + "/" + resultSet.getInt("id") +
                        ",Вид:" + resultSet.getString("vidk") +
                        ",Наим.:" + resultSet.getString("namek") +
                        ",Товар:" + resultSet.getString("prod") +
                        ",кол-во=" + resultSet.getBigDecimal("quantity") +
                        " " + resultSet.getString("unit") +
                        ",цена=" + resultSet.getBigDecimal("price") +
                        " руб");
            } else {
                System.out.println(resultSet.getString("action") +
                        " N документа " + resultSet.getString("numDoc") + "/" + resultSet.getInt("id") +
                        ",Вид:" + resultSet.getString("vidd") +
                        ",Наим.:" + resultSet.getString("named") +
                        ",Товар:" + resultSet.getString("prod") +
                        ",кол-во=" + resultSet.getBigDecimal("quantity") +
                        " " + resultSet.getString("unit") +
                        ",цена=" + resultSet.getBigDecimal("price") +
                        " руб");
            }
        }
    }
}
