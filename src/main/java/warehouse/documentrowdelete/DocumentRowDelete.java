package warehouse.documentrowdelete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DocumentRowDelete {
    public static void rowDel(Connection connection) throws Exception {
        final String sqlDelete = """
                delete from documents
                where id=?;
                """;
        PreparedStatement statement;
        System.out.println("Удаление строки из документа");
        int result;
        statement = connection.prepareStatement(sqlDelete);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id строки документа для удаления");
        if (scanner.hasNext()) {
            int intId = scanner.nextInt();
            System.out.println("Вы ввели id строки %d".formatted(intId));
            statement.setInt(1, intId);
            try {
                result = statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Ошибка ввода: " + e.toString());
                return;
            }
            System.out.println("Количество удаленных записей: " + result);
        }
        System.out.println("");

    }
}
