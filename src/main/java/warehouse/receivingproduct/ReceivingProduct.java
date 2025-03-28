package warehouse.receivingproduct;

import warehouse.addnewprod.AddNewProd;
import warehouse.balanceview.BalanceView;
import warehouse.documententer.DocumentEnter;
import warehouse.documentrowdelete.DocumentRowDelete;
import warehouse.documentview.DocumentView;
import warehouse.util.DbHelper;
import warehouse.util.OperDay;
import warehouse.viewingclients.ViewingClients;
import warehouse.viewingproducts.ViewingProducts;
import warehouse.viewingunitsmeasur.ViewingUnitsMeasur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import static warehouse.util.OperDay.opdate;

public class ReceivingProduct {
    public static void addProduct() throws Exception {
        System.out.println("");
        try (Connection connection = DbHelper.createConnection()) {
            if (OperDay.setOpdate(connection)) {
                System.out.println("Получили опердень:" + opdate);
            } else {
                System.out.println("Ошибка получения опердня");
                return;
            }
            Scanner scanner = new Scanner(System.in);
            int menu = 0;
            PreparedStatement statement;
            PreparedStatement statementHelp;
            ResultSet resultSet = null;
            int result = 0;
            do {
                try {
                    System.out.println("");
                    System.out.println("Приход товара от поставщиков");
                    System.out.println("  для выбора режима введите номер пункта меню");
                    System.out.println("1.Просмотр справочника существующих поставщиков");
                    System.out.println("2.Просмотр справочника существующей продукции");
                    System.out.println("3.Просмотр справочника единиц измерения");
                    System.out.println("4.Ввод новой продукции в справочник");
                    System.out.println("5.Ввод документа на приход товара на склад от поставщика за текущее число");
                    System.out.println("6.Просмотр документа на приход товара на склад от поставщика за текущее число");
                    System.out.println("7.Удаление строки из документа");
                    System.out.println("8.Просмотр остатков на складе за текущее число");
                    System.out.println("9.Выход");
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
                                ViewingProducts.viewProd(connection);
                                menu = 0;
                                break;
                            }
                            case 3: {
                                ViewingUnitsMeasur.viewUnits(connection);
                                menu = 0;
                                break;
                            }
                            case 4: {
                                AddNewProd.newProduct(connection);
                                menu = 0;
                                break;
                            }
                            case 5: {
                                DocumentEnter.addDoc(connection);
                                menu = 0;
                                break;
                            }
                            case 6: {
                                DocumentView.dovView(connection, true, true);
                                menu = 0;
                                break;
                            }
                            case 7: {
//                                DocumentRowDelete.rowDel(connection);
                                menu = 0;
                                break;
                            }
                            case 8: {
                                BalanceView.balView(connection, true, 0);
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
