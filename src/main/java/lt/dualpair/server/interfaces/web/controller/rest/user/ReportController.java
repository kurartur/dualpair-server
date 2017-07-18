package lt.dualpair.server.interfaces.web.controller.rest.user;

import lt.dualpair.core.user.User;
import lt.dualpair.core.user.UserReport;
import lt.dualpair.core.user.UserReportRepository;
import lt.dualpair.core.user.UserRepository;
import lt.dualpair.server.interfaces.web.authentication.ActiveUser;
import lt.dualpair.server.security.UserDetails;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class ReportController {

    private static final int MAX_REPORT_COUNT_PER_DAY = 5;

    private UserReportRepository userReportRepository;
    private UserRepository userRepository;

    public ReportController(UserReportRepository userReportRepository, UserRepository userRepository) {
        this.userReportRepository = userReportRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/report")
    public ResponseEntity report(@RequestBody Map<String, String> data, @ActiveUser UserDetails principal) {
        Long userId = Long.valueOf(data.get("user_id"));
        Assert.notNull(userId, "User id not specified");

        User principalUser = userRepository.findById(principal.getId()).get();
        int reportCount = userReportRepository.getReportCountByUser(principalUser, DateUtils.addDays(new Date(), -1));
        if (reportCount >= MAX_REPORT_COUNT_PER_DAY) {
            throw new IllegalStateException("Report limit is reached");
        }
        User userBeingReported = userRepository.findById(userId).orElseThrow((Supplier<RuntimeException>) () -> new IllegalArgumentException("User not found"));
        if (userReportRepository.findUserReportByUser(userBeingReported, principalUser).isPresent()) {
            throw new IllegalStateException("User already reported");
        }
        userReportRepository.save(new UserReport(userBeingReported, principalUser));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
