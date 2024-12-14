package top.mrxiaom.figura.authprovider.perm;

public class NoProvider implements IPermissionProvider {
    public static final NoProvider INSTANCE = new NoProvider();
    private NoProvider() {
    }
    @Override
    public Boolean playerHas(String playerName, String permission) {
        return null;
    }
}
