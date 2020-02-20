package calculator;

import javafx.beans.binding.StringBinding;

import java.math.BigInteger;
import java.util.*;

public class Main {
    public static  Map<String, BigInteger> variables = new HashMap<>();

    public static void main(String[] args) {



        Scanner scanner = new Scanner(System.in);
        String str = "";
        str = scanner.nextLine();
        String numberMinus = "[0-9]+\\s*-+\\s*[0-9]{0}\\s*\\+*";
        String numberPlus = "[0-9]*+\\s*\\++\\s*[0-9]{0}\\s*-*";
        String numberthree = "\\d+\\s*(\\+{0}|\\s*-{0})\\d+";
        String potentialVariable ="^\\s*\\d*[a-zA-Z]+\\d*\\s*=*\\s*\\d*\\s*[a-zA-Z]*\\s*\\d*\\s*=*\\s*\\d*\\s*\\w*\\s*";
        String simpleNumber = "\\s*\\+*\\s*-*\\s*[0-9]+\\s*\\+*\\s*-*\\s*";
        String plus = "[0-9]+\\s*\\++";
        String minus = "[0-9]+\\s*-+";
        String minusplus = "[0-9]+\\s*-+\\s*\\++";
        String plusminus = "[0-9]+\\d+\\s*\\++\\s*-+";
        String command = "/[a-zA-Z]*";





        while (!str.equals("/exit")) {
            if (!str.contains(" ") && !str.matches(command)) {
                StringBuilder sb = new StringBuilder();
                char[] chars = str.toCharArray();
                for (Character c :
                        chars) {
                    sb.append(c + " ");
                }
                str = sb.toString().trim();
            }
            boolean isOkay = true;

            String firstclean = str.replaceAll("-\\s*", "-")
                    .replaceAll("\\s+", " ")
                    .replaceAll("\\+\\s\\+", "+ ")
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "");

            if (firstclean.matches(numberMinus) || firstclean.matches(numberPlus) || firstclean.matches(numberthree) || firstclean.matches(plus) || firstclean.matches(minus) || firstclean.matches(plusminus) || firstclean.matches(minusplus)) {
                System.out.println("Invalid expression");

            } else if (firstclean.matches(potentialVariable)) {
                String declarationOfVariable = "^\\s*[a-zA-Z]+\\s*=+\\s*\\d*[a-zA-Z]*\\d*[a-zA-Z]*";
                String simpleVariable = "^\\s*[a-zA-Z]+\\s*";


                if (firstclean.matches(declarationOfVariable)) {

                    String[] var = firstclean.replaceAll("\\s*=\\s*", (" = "))
                            .split("\\s");


                    if (var.length == 3 && var[0].matches(simpleVariable)) {
                        try {

                            variables.put(var[0], new BigInteger(var[2]));
                        } catch (Exception exp) {

                            if (var[2].matches(simpleVariable)) {
                                if (variables.containsKey(var[2])) {
                                    variables.put(var[0], variables.get(var[2]));
                                } else {
                                    System.out.println("Invalid assignment");
                                    isOkay = false;
                                }
                            } else {
                                System.out.println("Invalid assignment");
                                isOkay = false;
                            }

                        }
                    } else if (var.length == 3 && !var[0].matches(simpleVariable)) {
                        System.out.println("Invalid identifier");
                    } else {
                        System.out.println("Invalid assignment");
                        isOkay = false;
                    }
                }

                else if (firstclean.matches(simpleVariable)) {
                    if (variables.containsKey(firstclean.trim())) {
                        System.out.println(variables.get(firstclean));
                    } else {
                        System.out.println("Unknown variable");
                        isOkay = false;
                    }
                } else if (firstclean.trim().matches(potentialVariable)) {

                    String[] str1 = firstclean.split("\\s");
                    if (!str1[0].matches(simpleVariable)) {
                        System.out.println("Invalid identifier");
                    } else
                    {
                        System.out.println("Invalid assignment");
                    }
                }
                else {
                    System.out.println("Unknown variable");
                    isOkay = false;
                }
            }

            else if (firstclean.matches(simpleNumber)) {
                String secondcleann = firstclean.replaceAll("-\\s*", "-")
                        .replaceAll("--","")
                        .replaceAll("\\+", "")
                        .replaceAll("\\s+", "");
                System.out.println(secondcleann);
            } else if (str.matches(command)) {
                if (str.equals("/help")) {
                    System.out.println("Two -- give you a +");
                }
                else {
                    System.out.println("Unknown command");
                }
            } else if (str.contains(" ")) {

                String secondclean = str
                        .replaceAll("\\s+", " ")
                        .replaceAll("-\\s*-"," + ")
                        .replaceAll("\\++", "+")
                        .replaceAll("\\s+", " ");
                String postfix = getPostfix(secondclean);
                if (postfix.contains("(") || postfix.contains(")") || getResult(postfix).equals("null") ) {
                    System.out.println("Invalid expression");
                } else {
                    System.out.println(getResult(postfix));
                }
            }
            str = scanner.nextLine();
            if (str.equals("/exit")) {
                System.out.println("Bye!");
            }
        }
    }


    public static String getPostfix(String str){
        str = str.replaceAll("\\(", "( ")
                .replaceAll("\\)", " )");
        Deque<String> queue = new ArrayDeque<>();
        StringBuilder result = new StringBuilder();
        String[] array = str.split("\\s");


        for (String string :
                array) {
            boolean isVariable = false;
            BigInteger i = null;
            if (string.matches("[a-zA-z]+")) {
                if (variables.containsKey(string)) {
                    i = variables.get(string);
                    isVariable = true;
                } else {
                    System.out.println("Unknown variable");
                }
            }
            try {
                if (isVariable) {
                    result.append(String.valueOf(i) + " ");
                } else {
                    i = new BigInteger(string);
                    result.append(string + " ");
                }
            } catch (Exception e) {

                if (queue.isEmpty()) {
                    queue.offerLast(string);
                } else if (string.equals("(")) {
                    queue.offerLast(string);
                } else if (string.equals(")")) {

                    while (!queue.peekLast().equals("(")) {
                        result.append(queue.pollLast() + " ");
                        if (queue.isEmpty()) {
                            return "Invalid expression";
                        }
                    }
                    queue.pollLast();

                } else if (string.equals("*") || string.equals("/")) {

                    while (!queue.isEmpty() && !queue.peekLast().equals("(") && !queue.peekLast().equals("+") && !queue.peekLast().equals("-")) {
                        result.append(queue.pollLast() + " ");
                    }
                    queue.offerLast(string);

                } else if (string.equals("+") || string.equals("-")) {
                    while (!queue.isEmpty() && !queue.peekLast().equals("(")) {
                        result.append(queue.pollLast() + " ");
                    }
                    queue.offerLast(string);
                }
            }
        }

        while(!queue.isEmpty()){
            result.append(queue.pollLast() + " ");
        }

        return result.toString().trim();
    }

    public static String getResult(String string) {
        StringBuilder result = new StringBuilder();
        Deque<String> deque = new ArrayDeque<>();
        String[] postfix = string.split("\\s");


        for (String s :
                postfix) {
            try {
                BigInteger i = new BigInteger(s);

                deque.offerLast(s);
            } catch (Exception exp){

                if (s.equals("*")) {
                    BigInteger a = new BigInteger(deque.pollLast());
                    BigInteger b = new BigInteger(deque.pollLast());
                    deque.offerLast(String.valueOf(a.multiply(b)));

                } else if (s.equals("/")) {
                    BigInteger a = new BigInteger(deque.pollLast());
                    BigInteger b = new BigInteger(deque.pollLast());
                    deque.offerLast(String.valueOf(b.divide(a)));

                }  else if (s.equals("-")) {
                    BigInteger a = new BigInteger(deque.pollLast());
                    BigInteger b = new BigInteger(deque.pollLast());
                    deque.offerLast(String.valueOf(b.subtract(a)));

                } else if (s.equals("+")) {
                    BigInteger a = new BigInteger(deque.pollLast());
                    BigInteger b = new BigInteger(deque.pollLast());
                    deque.offerLast(String.valueOf(b.add(a)));
                }
            }
        }
        if (deque.size() > 1) {
            return "Invalid expression";
        }
        result.append(deque.pollLast());


        return result.toString();
    }
}
