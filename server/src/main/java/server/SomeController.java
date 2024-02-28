//package server;
//
//import commons.Expense;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@Controller
//@RequestMapping("/")
//public class SomeController {
//    Expense expense;
//    public SomeController(Expense expense){
//        this.expense = expense;
//    }
////    @GetMapping("/")
////    @ResponseBody
////    public String index() {
////        Expense expense = new Expense();
////        System.out.println(expense.toString());
////        return "Hello world!";
////    }
//
//    @GetMapping("/")
//    @ResponseBody
//    public String showExpense() {
//        // Use the autowired Expense bean
//        return expense.toString();
//    }
//}