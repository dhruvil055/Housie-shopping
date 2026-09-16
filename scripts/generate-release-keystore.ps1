# Housie Shopping Release Keystore Generator
# Generates an RSA 4096-bit release keystore valid for 10,000 days (satisfying Google Play 2050 requirement)

param (
    [string]$KeystorePath = "housie-release.keystore",
    [string]$Alias = "housieshopping",
    [string]$Password = "HousieShopping2026SecureKey",
    [string]$DName = "CN=Housie Shopping, OU=Mobile Engineering, O=Housie Shopping Pvt Ltd, L=Ahmedabad, ST=Gujarat, C=IN"
)

$jdkKeytool = "C:\Users\kyada\.jdks\jbr-17.0.14\bin\keytool.exe"
if (-not (Test-Path $jdkKeytool)) {
    $jdkKeytool = "keytool"
}

Write-Host "==> Checking if keystore exists at $KeystorePath..."
if (Test-Path $KeystorePath) {
    Write-Host "Keystore already exists at $KeystorePath. Skipping creation."
} else {
    Write-Host "==> Generating RSA 4096-bit release keypair..."
    & $jdkKeytool -genkeypair `
        -alias $Alias `
        -keyalg RSA `
        -keysize 4096 `
        -validity 10000 `
        -keystore $KeystorePath `
        -storepass $Password `
        -keypass $Password `
        -dname $DName

    if ($LASTEXITCODE -eq 0) {
        Write-Host "==> Release keystore successfully generated at: $KeystorePath" -ForegroundColor Green
    } else {
        Write-Error "Failed to generate release keystore."
        exit $LASTEXITCODE
    }
}

# Generate local keystore.properties
$keystoreProps = @"
storeFile=../$KeystorePath
storePassword=$Password
keyAlias=$Alias
keyPassword=$Password
"@

Set-Content -Path "keystore.properties" -Value $keystoreProps -Encoding utf8
Write-Host "==> keystore.properties created successfully." -ForegroundColor Green
Write-Host "==> Note: keystore.properties and $KeystorePath are in .gitignore."
