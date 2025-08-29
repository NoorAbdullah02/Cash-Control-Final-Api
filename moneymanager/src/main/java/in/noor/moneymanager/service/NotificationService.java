package in.noor.moneymanager.service;

import in.noor.moneymanager.dto.ExpenseDTO;
import in.noor.moneymanager.entity.ProfileEntity;
import in.noor.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    //@Scheduled(cron = "0 * * * * *",zone = "Asia/Dhaka")
    @Scheduled(cron = "0 0 10  * * *",zone = "Asia/Dhaka")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles){
            String body = "Assalamualaikum / Adab  " + profile.getFullName() + ",<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Cash Control.<br><br>"
                    + "<a href=" + frontendUrl + " style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;'>Go to Cash Control</a><br><br>"
                    + "Best regards,<br>Cash Control Team";
            emailService.sendEmail(profile.getEmail(),"Daily reminder: Add your income and expenses", body);
        }
        log.info("Job completed: sendDailyIncomeExpenseReminder()");
    }
    //@Scheduled(cron = "0 * * * * *",zone = "Asia/Dhaka")
    @Scheduled(cron = "0 59 11 * * *",zone = "Asia/Dhaka")
    public void sendDailyExpenseSummary(){
        log.info("Job started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
          List<ExpenseDTO> todaysExpenses =  expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
            if (!todaysExpenses.isEmpty()) {
               StringBuilder table = new StringBuilder();
               table.append("<table style='border-collapse:collapse;width:100%'>");
                table.append("<th style='border: 1px solid #ddd; padding: 8px;'>S.No</th>"
                        + "<th style='border: 1px solid #ddd; padding: 8px;'>Name</th>"
                        + "<th style='border: 1px solid #ddd; padding: 8px;'>Amount</th>"
                        + "<th style='border: 1px solid #ddd; padding: 8px;'>Category</th>"
                        );
                int i =1;
                for(ExpenseDTO expense : todaysExpenses){
                    table.append("<tr>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(i++).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getName()).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getAmount()).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>")
                            .append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A")
                            .append("</td>")
                            //.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getDate()).append("</td>")
                            .append("</tr>");

                }
                table.append("</table>");
                String body = "Hi "+profile.getFullName()+",<br><br> Here is a summary of your expenses for today:<br><br>"+table+"<br><br>Best regards,<br/>Cash Control Team";
                emailService.sendEmail(profile.getEmail(), "Your daily Expense Summary", body);

            }
        }
        log.info("Job Completed: sendDailyExpenseSummary()");

    }
}
