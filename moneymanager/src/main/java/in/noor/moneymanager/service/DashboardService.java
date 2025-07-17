package in.noor.moneymanager.service;

import in.noor.moneymanager.dto.ExpenseDTO;
import in.noor.moneymanager.dto.IncomeDTO;
import in.noor.moneymanager.dto.RecentTransationDTO;
import in.noor.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String,Object> getDashboardData() {
     ProfileEntity profile = profileService.getCurrentProfile();
     Map<String,Object> returnValue = new LinkedHashMap<>();
     List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
     List<ExpenseDTO> latestExpenses = expenseService.getCurrentMonthExpensesForCurrentUser();
     List<RecentTransationDTO> recentTransactions = concat(latestIncomes.stream().map(income ->
                RecentTransationDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("income")
                        .build()),
             latestExpenses.stream().map(expense ->
                     RecentTransationDTO.builder()
                     .id(expense.getId())
                             .profileId(profile.getId())
                     .icon(expense.getIcon())
                     .name(expense.getName())
                     .amount(expense.getAmount())
                     .date(expense.getDate())
                     .createdAt(expense.getCreatedAt())
                     .updatedAt(expense.getUpdatedAt())
                     .type("expense")
                     .build()))
             .sorted((a, b)->{
                 int cmp = b.getDate().compareTo(a.getDate());
                 if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                     return b.getCreatedAt().compareTo(a.getCreatedAt());
                 }
                 return cmp;
             }).collect(Collectors.toList());
     returnValue.put("totalBalance",
             incomeService.getTotalIncomeForCurrentUser()
                     .subtract(expenseService.getTotalExpensesForCurrentUser()));
     returnValue.put("totalIncome",incomeService.getTotalIncomeForCurrentUser());
     returnValue.put("totalExpense",expenseService.getTotalExpensesForCurrentUser());
     returnValue.put("recent5Expenses",latestExpenses);
     returnValue.put("recent5Incomes",latestIncomes);
     returnValue.put("recentTransactions",recentTransactions);
     return returnValue;
    }
}
