package top.mrxiaom.figura.authprovider.perm;

import org.jetbrains.annotations.Nullable;

public interface IPermissionProvider {
    @Nullable
    Boolean playerHas(String playerName, String permission);
}
