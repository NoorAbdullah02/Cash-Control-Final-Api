package in.noor.moneymanager.service;

import in.noor.moneymanager.dto.ExpenseDTO;
import in.noor.moneymanager.entity.CategoryEntity;
import in.noor.moneymanager.entity.ExpenseEntity;
import in.noor.moneymanager.entity.ProfileEntity;
import in.noor.moneymanager.repository.CategoryRespository;
import in.noor.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor

public class ExpenseService {

    private final CategoryRespository categoryRespository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO dto) {
       ProfileEntity profile = profileService.getCurrentProfile();
       CategoryEntity category = categoryRespository.findById(dto.getCategoryId())
               .orElseThrow(() -> new RuntimeException("Category not found"));
       ExpenseEntity newExpense = toEntity(dto, profile, category);
       newExpense = expenseRepository.save(newExpense);
       return toDTO(newExpense);
    }

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
       ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }
    //detete expnace by id

    public void deleteExpense(Long expenseId) {
       ProfileEntity profile =  profileService.getCurrentProfile();
     ExpenseEntity entity =  expenseRepository.findById(expenseId)
               .orElseThrow(() -> new RuntimeException("Expense not found"));
     if(!entity.getProfile().getId().equals(profile.getId())) {
         throw new RuntimeException("Unable to delete expense");
     }
         expenseRepository.delete(entity);
    }

    // Get latest 5 Expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
       ProfileEntity profile = profileService.getCurrentProfile();
       List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
       return list.stream().map(this::toDTO).toList();
    }

    //Get total expenses of current user

    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
       BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
       return total != null ? total: BigDecimal.ZERO;
    }

    //Filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
    }

    // Notification
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
      List<ExpenseEntity> list =  expenseRepository.findByProfileIdAndDate(profileId,date);
      return list.stream().map(this::toDTO).toList();
    }


    //Helper Methods
    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category) {
       return ExpenseEntity.builder()
               .name(dto.getName())
               .icon(dto.getIcon())
               .amount(dto.getAmount())
               .date(dto.getDate())
               .profile(profile)
               .category(category)
               .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity entity) {
      return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
