module dev.hujiawei.assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens dev.AP.assignment to javafx.fxml;
    exports dev.AP.assignment;
}