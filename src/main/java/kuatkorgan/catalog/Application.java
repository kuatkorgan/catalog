package kuatkorgan.catalog;

import kuatkorgan.catalog.entity.Category;
import kuatkorgan.catalog.entity.Option;
import kuatkorgan.catalog.entity.Product;
import kuatkorgan.catalog.entity.Value;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Scanner;

public class Application {
    private static final Scanner IN = new Scanner(System.in);

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("main-connection");

    public static void main(String[] args) {
        System.out.println("Создание товара [1]");
        System.out.println("Обновление товара [2]");
        System.out.print("Выберите действие: ");
        String actionNumIn = IN.nextLine();
        switch (actionNumIn) {
            case "1" -> create();
            case "2" -> update();
            default -> System.out.println("Такого действия не существует");
        }
    }

    private static void create() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();

            System.out.println("--- ВЫБОР КАТЕГОРИИ ---");

            System.out.print("Введите название категории: ");
            String categoryName = IN.nextLine();

            TypedQuery<Category> categoryQuery = manager.createQuery(
                    "select c from Category c where c.name = ?1", Category.class);
            categoryQuery.setParameter(1, categoryName);
            categoryQuery.setMaxResults(1);

            List<Category> categoryList = categoryQuery.getResultList();

            Category category;

            if (categoryList.isEmpty()) {
                category = new Category();
                category.setName(categoryName);
                manager.persist(category);
            } else {
                category = categoryList.get(0);
            }

            System.out.println("--- СОЗДАНИЕ ТОВАРА ---");

            System.out.print("Введите название товара: ");
            String productName = IN.nextLine();
            System.out.print("Введите цену товара: ");
            String productPriceIn = IN.nextLine();
            double productPrice = Double.parseDouble(productPriceIn);
            System.out.print("Введите описание товара: ");
            String productDescription = IN.nextLine();

            Product product = new Product();
            product.setCategory(category);
            product.setName(productName);
            product.setPrice(productPrice);
            product.setDescription(productDescription);
            manager.persist(product);

            System.out.println("--- ХАРАКТЕРИСТИКИ ТОВАРА ---");

            List<Option> optionList = category.getOptions();

            for (Option option : optionList) {
                Value value = new Value();
                value.setProduct(product);
                value.setOption(option);
                System.out.print(option.getName() + ": ");
                String valueIn = IN.nextLine();
                value.setValue(valueIn);
                manager.persist(value);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void update() {
        EntityManager manager = FACTORY.createEntityManager();

        System.out.print("Введите ID продукта который хотите изменить: ");

        String productIdIn = IN.nextLine();
        long productId = Long.parseLong(productIdIn);
        Product product = manager.find(Product.class, productId);

        System.out.print("Введите новое название для товара [" + product.getName() + "]: ");
        String productName = IN.nextLine();
        if (productName.isEmpty()) {
            productName = product.getName();
        }

        System.out.print("Введите новую цену для товара [" + product.getPrice() + "]: ");
        String productPriceIn = IN.nextLine();
        double productPrice;
        if (productPriceIn.isEmpty()) {
            productPrice = product.getPrice();
        } else {
            productPrice = Double.parseDouble(productPriceIn);
        }

        System.out.print("Введите новое описание для товара: ");
        String productDescription = IN.nextLine();
        if (productDescription.isEmpty()) {
            productDescription = product.getDescription();
        }

        try {
            manager.getTransaction().begin();

            product.setName(productName);
            product.setDescription(productDescription);
            product.setPrice(productPrice);

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
