module Hotel {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.sql;
	requires javafx.base;
	requires junit;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
	opens application.controller.onglets to javafx.fxml;
	opens application.controller.sousOnglets to javafx.fxml;
	opens JUnitPackage to junit, javafx.fxml, java.sql;
}