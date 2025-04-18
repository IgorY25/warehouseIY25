package warehouse.transferproducts;

import warehouse.balanceview.BalanceView;
import warehouse.documentview.DocumentView;
import warehouse.transferproductsinvoice.TransferProductsInvoice;
import warehouse.util.DbHelper;
import warehouse.util.OperDay;
import warehouse.viewingclients.ViewingClients;

import java.sql.Connection;
import java.util.Scanner;

import static warehouse.util.OperDay.opdate;

public class TransferProducts {
    public static void transProd() throws Exception {
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
            do {
                try {
                    System.out.println("");
                    System.out.println("Расход товара со склада");
                    System.out.println("  для выбора режима введите номер пункта меню");
                    System.out.println("1.Просмотр справочника существующих подразделений");
                    System.out.println("2.Выдача товара со склада в магазины");
                    System.out.println("3.Просмотр документа на расход товара со склада в магазины за текущее число");
                    System.out.println("4.Просмотр остатков на складе");
                    System.out.println("5.Выход");
                    if (scanner.hasNext()) {
                        menu = scanner.nextInt();
                        System.out.println("Вы ввели число %d".formatted(menu));
                        switch (menu) {
                            case 1: {
                                ViewingClients.viewSeller(connection, false);
                                menu = 0;
                                break;
                            }
                            case 2: {
                                TransferProductsInvoice.transInvoice(connection, true);
                                menu = 0;
                                break;
                            }
                            case 3: {
                                DocumentView.dovView(connection, false, false);
                                menu = 0;
                                break;
                            }
                            case 4: {
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
