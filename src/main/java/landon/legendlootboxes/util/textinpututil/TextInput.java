package landon.legendlootboxes.util.textinpututil;

import landon.legendlootboxes.util.c;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class TextInput {
    private String prompt;
    private ResponseType accepts;
    private CompletePrompt partOf;
    private String rawResponse;
    private Object response;

    public TextInput(String prompt, ResponseType accepts) {
        this.prompt = prompt;
        this.accepts = accepts;
    }

    public void run() {
        this.partOf.getPlayer().sendMessage(c.c("&a&l(" + this.partOf.getCurrent() + "/" + this.partOf.getTextInputs().size() + "): &a" + this.prompt));
    }
}
