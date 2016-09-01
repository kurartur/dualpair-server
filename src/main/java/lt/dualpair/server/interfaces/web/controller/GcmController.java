package lt.dualpair.server.interfaces.web.controller;

import lt.dualpair.server.domain.model.user.User;
import lt.dualpair.server.infrastructure.notification.Notification;
import lt.dualpair.server.infrastructure.notification.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Profile("!prod")
public class GcmController {

    @Autowired
    private NotificationSender notificationSender;

    @RequestMapping("/gcm")
    public String index() {
        return "gcm";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/gcm/message/send")
    public ModelAndView sendMessage(@RequestParam String message) {
        ModelAndView modelAndView = new ModelAndView("gcm");
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Notification notification = new Notification(user.getId(), message);
        notificationSender.sendNotification(notification);
        modelAndView.addObject("pageMessage", "Sent " + notification);
        return modelAndView;
    }

}
