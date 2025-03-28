package warehouse.clientservice;

import warehouse.util.DbHelper;
import warehouse.viewingclients.ViewingClients;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientService {
    public static void addClient() throws Exception {
        System.out.println("");
        final String sqlSelectType = """
                select tc.id, tc.name from type_client tc
                where external;
                """;
        final String sqlSelectTypePar = """
                select count(id) as count
                from type_client
                where external and id = ?;
                """;

        final String sqlInsert = """
                insert into clients (type_client_ref,name)
                values (?, ?);
                """;
        final String sqlUpdate = """
                update clients
                set name = ?,
                    type_client_ref = ?
                where id=?;
                """;
        final String sqlDelete = """
                delete from clients
                where id=?;
                """;
        try (Connection connection = DbHelper.createConnection()) {
            Scanner scanner = new Scanner(System.in);
            int menu = 0;
            PreparedStatement statement;
            PreparedStatement statementHelp;
            ResultSet resultSet = null;
            int result = 0;
            do {
                try {
                    System.out.println("Для выбора режима введите номер пункта меню");
                    System.out.println("1.Просмотр существующих поставщиков");
                    System.out.println("2.Просмотр видов организаций");
                    System.out.println("3.Ввод нового поставщика");
                    System.out.println("4.Изменение поставщика");
                    System.out.println("5.Удаление поставщика");
                    System.out.println("6.Выход");
                    if (scanner.hasNext()) {
                        menu = scanner.nextInt();
                        System.out.println("Вы ввели число %d".formatted(menu));
                        switch (menu) {
                            case 1: {
                                ViewingClients.viewSeller(connection, true);
                                menu = 0;
                                break;
                            }
                            case 2: {
                                System.out.println("case 2");
                                statement = connection.prepareStatement(sqlSelectType);
                                resultSet = statement.executeQuery();
                                System.out.println("");
                                System.out.println("Вывод видов организаций");
                                while (resultSet.next()) {
                                    int number = resultSet.getInt("id");
                                    String name = resultSet.getString("name");
                                    System.out.println("id=" + number + ",Вид организации='" + name);
                                }
                                System.out.println("Конец вывода видов организаций");
                                System.out.println("");
                                menu = 0;
                                break;
                            }
                            case 3: {
                                System.out.println("case 3 Ввод нового поставщика");
                                statement = connection.prepareStatement(sqlInsert);
                                statementHelp = connection.prepareStatement(sqlSelectTypePar);
                                System.out.println("Введите id вида организпции");
                                if (scanner.hasNext()) {
                                    int intRes = scanner.nextInt();
                                    System.out.println("Вы ввели id %d".formatted(intRes));

                                    statementHelp.setInt(1, intRes);
                                    try {
                                        resultSet = statementHelp.executeQuery();
                                        resultSet.next();
                                        result = resultSet.getInt("count");
                                        if (result > 0) {
                                            System.out.println("id вида организации введен успешно");
                                        } else {
                                            System.out.println("Ошибка ввода id вида организации");
                                            break;
                                        }
                                    } catch (SQLException e) {
                                        System.out.println("Ошибка ввода: " + e.toString());
                                        break;
                                    }

                                    String strRet = null;
                                    if (scanner.hasNextLine()) {
                                        scanner.nextLine();
                                    }
                                    System.out.println("Введите наименование поставщика");
                                    if (scanner.hasNextLine()) {
                                        strRet = scanner.nextLine();
                                        System.out.println("Вы ввели наименование " + strRet);

                                        statement.setInt(1, intRes);
                                        statement.setString(2, strRet);
                                        try {
                                            result = statement.executeUpdate();
                                        } catch (SQLException e) {
                                            System.out.println("Ошибка ввода: " + e.toString());
                                            break;
                                        }
                                        System.out.println("Количество введенных записей: " + result);
                                    }
                                }
                                System.out.println("");
                                menu = 0;
                                break;
                            }
                            case 4: {
                                System.out.println("case 4");
                                statement = connection.prepareStatement(sqlUpdate);
                                System.out.println("Введите id поставщика для редактирования");
                                if (scanner.hasNext()) {
                                    int intId = scanner.nextInt();
                                    System.out.println("Вы ввели id поставщика %d".formatted(intId));

                                    System.out.println("Введите id вида организпции");
                                    if (scanner.hasNext()) {
                                        int intRes = scanner.nextInt();
                                        String strRet = null;
                                        System.out.println("Вы ввели id %d".formatted(intRes));

                                        if (scanner.hasNextLine()) {
                                            scanner.nextLine();
                                        }
                                        System.out.println("Введите наименование поставщика");
                                        if (scanner.hasNextLine()) {
                                            strRet = scanner.nextLine();
                                            System.out.println("Вы ввели наименование " + strRet);

                                            statement.setString(1, strRet);
                                            statement.setInt(2, intRes);
                                            statement.setInt(3, intId);
                                            try {
                                                result = statement.executeUpdate();
                                            } catch (SQLException e) {
                                                System.out.println("Ошибка ввода: " + e.toString());
                                                break;
                                            }
                                            System.out.println("Количество введенных записей: " + result);
                                        }
                                    }
                                }
                                System.out.println("");
                                menu = 0;
                                break;
                            }
                            case 5: {
                                System.out.println("case 5");
                                statement = connection.prepareStatement(sqlDelete);
                                System.out.println("Введите id поставщика для удаления");
                                if (scanner.hasNext()) {
                                    int intId = scanner.nextInt();
                                    System.out.println("Вы ввели id поставщика %d".formatted(intId));
                                    statement.setInt(1, intId);
                                    try {
                                        result = statement.executeUpdate();
                                    } catch (SQLException e) {
                                        System.out.println("Ошибка ввода: " + e.toString());
                                        break;
                                    }
                                    System.out.println("Количество удаленных записей: " + result);
                                }
                                System.out.println("");
                                menu = 0;
                                break;
                            }
                            default: {
                                System.out.println("default:");
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    menu = 0;
                    System.out.println("Ошибка ввода " + e.toString());
                    scanner = new Scanner(System.in);
                }
            }
            while (menu == 0);
            System.out.println("Выход");
            connection.commit();
        }
    }
}
