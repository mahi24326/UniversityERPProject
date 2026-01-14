package edu.univ.erp.access;

import edu.univ.erp.data.AdminRepository;

public class MaintenanceAccess {

    private static final MaintenanceAccess instance = new MaintenanceAccess();
    private final AdminRepository repo;

    // Cache the current maintenance mode (prevents reload issues)
    private boolean maintenanceMode;

    private MaintenanceAccess() {
        this.repo = new AdminRepository();
        this.maintenanceMode = repo.getMaintenanceMode(); // load from DB once at startup
    }

    public static MaintenanceAccess getInstance() {
        return instance;
    }


    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }


    public void reloadFromDatabase() {
        this.maintenanceMode = repo.getMaintenanceMode();
    }


    public void setMaintenanceMode(boolean enable) {
        repo.setMaintenanceMode(enable);   // persist in DB
        this.maintenanceMode = enable;      // update cached value
    }

    public boolean toggleMaintenanceMode() {
        boolean newValue = !maintenanceMode;
        setMaintenanceMode(newValue);
        return newValue;
    }

    public boolean canModifyData() {
        return !maintenanceMode;
    }

    public boolean canStudentViewOnly() {
        return true;
    }
}
