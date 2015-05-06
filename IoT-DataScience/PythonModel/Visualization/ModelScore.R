library(ggplot2)
library(dplyr)

scores <- read.csv("../Output/Models/modelscore.csv")

scores$minute <- factor(scores$minute)

scores_grouped <- scores %>% group_by(minute) %>% summarise(med=median(log_loss))

plot <- ggplot(scores_grouped, aes(minute, med)) + geom_point() +
    xlab("Minute") + ylab("Median Log-Loss") + theme(text=element_text(size=20))
plot
ggsave("log-loss.pdf", width=16, height=9)
