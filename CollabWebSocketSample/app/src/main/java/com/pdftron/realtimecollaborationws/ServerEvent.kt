package com.pdftron.realtimecollaborationws

sealed class ServerEvent {
    class importXfdfCommand(val xfdfCommand: String) : ServerEvent()
}
