package warehouse;

import lombok.SneakyThrows;
import warehouse.balancesheet.BalanceSheet;
import warehouse.clientservice.ClientService;
import warehouse.receivingproduct.ReceivingProduct;
import warehouse.saleproducts.SaleProducts;
import warehouse.transferproducts.TransferProducts;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;


public class MenuRunner {
    public static final Properties DB_PROPERTIES = new Properties();

    static {
        loadDbProperties();
    }

    @SneakyThrows(IOException.class)
    private static void loadDbProperties() {
        DB_PROPERTIES.load(MenuRunner.class.getResourceAsStream("/db.properties"));
    }

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");
        Scanner scanner = new Scanner(System.in);
        int menu = 0;
        do {
            try {
                System.out.println("Головное меню");
                System.out.println("  для выбора режима введите номер пункта меню");
                System.out.println("1.Ввод поставщиков");
                System.out.println("2.Ввод подразделений");
                System.out.println("3.Приход товара от поставщиков");
                System.out.println("4.Расход товара со склада");
                System.out.println("5.Продажа товара");
                System.out.println("6.Оборотно-сальдовая ведомость");
                System.out.println("7.Прейскурант");
                System.out.println("8.Перевод дня");
                System.out.println("9.Выход");
                if (scanner.hasNext()) {
                    menu = scanner.nextInt();
                    System.out.println("Вы ввели число %d".formatted(menu));
                    switch (menu) {
                        case 1: {
                            ClientService.addClient();
                            menu = 0;
                            break;
                        }
                        case 2: {
                            break;
                        }
                        case 3: {
                            ReceivingProduct.addProduct();
                            menu = 0;
                            break;
                        }
                        case 4: {
                            TransferProducts.transProd();
                            menu = 0;
                            break;
                        }
                        case 5: {
                            SaleProducts.saleProd();
                            menu = 0;
                            break;
                        }
                        case 6: {
                            BalanceSheet.balSheet();
                            menu = 0;
                            break;
                        }
                        default: {
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
    }
}
