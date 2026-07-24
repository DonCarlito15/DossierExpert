package com.DRJ.dossierexpert.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class BottomBarController implements Initializable {

    @FXML private Text statusLabel;
    @FXML private Text infoLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStatus("✅ جاهز");
        setInfo("عدد الملفات : 0");
        System.out.println("✅ BottomBarController initialisé");
    }

    public void setStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }

    public void setStatusSuccess(String message) {
        setStatus("✅ " + message);
    }

    public void setStatusError(String message) {
        setStatus("❌ " + message);
    }

    public void setStatusWarning(String message) {
        setStatus("⚠️ " + message);
    }

    public void setStatusInfo(String message) {
        setStatus("ℹ️ " + message);
    }

    public void showProcessing(String message) {
        setStatus("⏳ " + message);
    }

    public void setInfo(String info) {
        if (infoLabel != null) {
            infoLabel.setText(info);
        }
    }

    public void setInfoWithCount(int count) {
        setInfo("عدد الملفات : " + count);
    }

    public void setInfoWithText(String text) {
        setInfo(text);
    }

    public void setInfoWithDossier(String dossierNumber) {
        setInfo("ملف : " + dossierNumber);
    }

    public void reset() {
        setStatus("✅ جاهز");
        setInfo("عدد الملفات : 0");
    }

    public String getStatus() {
        return statusLabel != null ? statusLabel.getText() : "";
    }

    public String getInfo() {
        return infoLabel != null ? infoLabel.getText() : "";
    }
}