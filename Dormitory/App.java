import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public final class App {
  private App() {
  }

  List<Item> items;

  public static void main(String[] args) {
    App app = new App();
    app.startApp();
  }

  private void startApp() {
    items = new LinkedList<Item>();
    printWelcomMessages();
    handleUserSelect();
  }

  void printWelcomMessages() {
    System.out.println("*******************************************");
    System.out.println("**\t   STUDENT EXPENSE SYSTEM        **");
    System.out.println("*******************************************");
    System.out.println("**\t[1] Sign up                      **");
    System.out.println("**\t[2] Login                        **");
    System.out.println("**\t[3] Checkout                     **");
    System.out.println("**\t[4] Exit                         **");
    System.out.println("*******************************************");
    System.out.println("*******************************************");
  }

  void handleUserSelect() {
    String userName;
    String password;

    switch (getUserInt()) {
      case 1:
        System.out.println("please enter a valid user name");
        userName = getUserString();
        System.out.println("please enter a valid password");
        password = getUserString();
        encrypThenAddUsernameAndPasswordToFile(userName, password);
        System.out.println("Sign up succeded");
        printWelcomMessages();
        handleUserSelect();
        break;
      case 2:
        System.out.println("pleas enter your name:");
        userName = getUserString().trim();
        System.out.println("pleas enter your password:");
        password = getUserString();
        if (validateNameAndPassword(userName, password)) {
          enterStudentDashboard(userName);
        } else {
          printWelcomMessages();
          handleUserSelect();
        }

        break;
      case 3:
        double total = 0.0;
        List<String> studentNames = new LinkedList<>();
        for (int i = 0; i < items.size(); i++) {
          total += items.get(i).price;
          if (!studentNames.contains(items.get(i).buyer)) {
            studentNames.add(items.get(i).buyer);
          }
        }
        System.out.println("each user should pay average of: " + total / studentNames.size() + "$");
        System.out.println("the checkout is reset");
        items.clear();
        printWelcomMessages();
        handleUserSelect();
        break;
      case 4:
        System.exit(0);
        break;
    }

  }

  private void handleStudentSelect(String userName) {
    String itemName;
    Scanner scanner = new Scanner(System.in);

    int select = scanner.nextInt();
    switch (select) {
      case 1:
        System.out.println("enter Item name");
        itemName = scanner.next();
        System.out.println("enter Item price");
        double itemPrice = getuserDouble();
        System.out.println("enter Item date");
        String itemdate = getUserString();

        System.out.println("enter Item quantity");
        int itemQuantity = getUserInt();
        Item item1 = new Item(itemName, userName, itemPrice, itemdate, itemQuantity);
        items.add(item1);
        System.out.println("item added");
        enterStudentDashboard(userName);
        break;
      case 2:
        System.out.print("enter Item name");
        itemName = scanner.next();
        removeItem(itemName, userName);
        enterStudentDashboard(userName);
        break;
      case 3:
        viewUserItems(userName);
        enterStudentDashboard(userName);
        break;
      case 4:
        viewAllItems();
        enterStudentDashboard(userName);
        break;
      case 5:
        viewCheckOut(userName);
        enterStudentDashboard(userName);
        break;
      case 6:
        printWelcomMessages();
        handleUserSelect();
        break;
    }

  }

  private void enterStudentDashboard(String userName) {
    System.out.println("*******************************************");
    System.out.println("**\t   Welcome: " + userName + "            ****");
    System.out.println("*******************************************");
    System.out.println("**\t[1] Add Item                     **");
    System.out.println("**\t[2] Delete Item                  **");
    System.out.println("**\t[3] View my bill                 **");
    System.out.println("**\t[4] View all bills               **");
    System.out.println("**\t[5] view checkout                **");
    System.out.println("**\t[6] Return                       **");
    System.out.println("*******************************************");
    System.out.println("*******************************************");
    handleStudentSelect(userName);
  }

  private void viewCheckOut(String userName) {
    double total = 0.0;
    List<String> studentNames = new LinkedList<>();
    for (int i = 0; i < items.size(); i++) {
      total += items.get(i).price;
      if (!studentNames.contains(items.get(i).buyer)) {
        studentNames.add(items.get(i).buyer);
      }
    }
    double currentUserMoneySpend = 0.0;
    for (int i = 0; i < items.size(); i++) {
      if (items.get(i).buyer.equals(userName)) {
        currentUserMoneySpend += items.get(i).price;
      }
    }

    double checkout = currentUserMoneySpend - (total / studentNames.size());
    if (checkout < 0) {
      System.out.println("you have to pay " + checkout * -1 + "$");
    } else if (checkout > 0) {
      System.out.println("you have to receive " + checkout + "$");
    } else {
      System.out.println("its already equal");
    }
  }

  private void viewAllItems() {
    for (int i = 0; i < items.size(); i++) {
      System.out.println("item name: " + items.get(i).name);
      System.out.println("item buyer: " + items.get(i).buyer);
      System.out.println("item price: " + items.get(i).price);
      System.out.println("item date: " + items.get(i).date);
      System.out.println("item quantity: " + items.get(i).quantity + "\n---------------------------");
    }
  }

  private void viewUserItems(String userName) {
    for (int i = 0; i < items.size(); i++) {
      if (items.get(i).buyer.equals(userName)) {
        System.out.println("item name: " + items.get(i).name);
        System.out.println("item buyer: " + items.get(i).buyer);
        System.out.println("item price: " + items.get(i).price);
        System.out.println("item date: " + items.get(i).date);
        System.out.println("item quantity: " + items.get(i).quantity + "\n---------------------------");
      }
    }
  }

  private void removeItem(String itemName, String userName) {
    for (int i = 0; i < items.size(); i++) {
      if (items.get(i).name.equals(itemName) && items.get(i).buyer.equals(userName)) {
        items.remove(items.get(i));
        System.out.println("item removed");
      }

    }
  }

  private boolean encrypThenAddUsernameAndPasswordToFile(String name, String password) {
    try {
      try (FileWriter fw = new FileWriter("accounts.txt", true);
          BufferedWriter bw = new BufferedWriter(fw);
          PrintWriter out = new PrintWriter(bw)) {
        out.print(name + " ");
        out.println(password);
      } catch (IOException e) {
        System.out.println(e.getLocalizedMessage());
      }
    } catch (Exception e) {

    }
    return true;
  }

  private boolean validateNameAndPassword(String name, String password) {
    boolean isNameAvailable = false;
    boolean isPasswordAvailable = false;
    try {
      FileReader fr = new FileReader("accounts.txt");
      BufferedReader bf = new BufferedReader(fr);
      String line = bf.readLine();
      while (line != null) {
        String nameTemp = line.substring(0, line.lastIndexOf(" "));
        if (nameTemp.equals(name)) {
          isNameAvailable = true;
        }

        String passwordTemp = line.substring(line.lastIndexOf(" ") + 1);
        if (passwordTemp.equals(password)) {
          isPasswordAvailable = true;
        }
        if (isNameAvailable && isPasswordAvailable) {
          return true;
        }
        isNameAvailable = false;
        isPasswordAvailable = false;
        line = bf.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  private int getUserInt() {
    try {
      Scanner scanner = new Scanner(System.in);
      int temp = scanner.nextInt();
      return temp;
    } catch (Exception e) {
      System.out.println("please enter a valid number");
      return getUserInt();
    }
  }

  private double getuserDouble() {
    Scanner scanner = new Scanner(System.in);
    double temp = scanner.nextDouble();
    return temp;
  }

  private String getUserString() {
    try {
      Scanner scanner = new Scanner(System.in);
      String temp = scanner.nextLine();
      return temp;
    } catch (Exception e) {
      System.out.println("please enter a valid text");
      return getUserString();
    }
  }

  public class Item {
    String name;
    String buyer;
    double price;
    String date;
    int quantity;

    public Item(String name, String buyer, double price, String date, int quantity) {
      this.name = name;
      this.buyer = buyer;
      this.price = price;
      this.date = date;
      this.quantity = quantity;
    }
  }

}