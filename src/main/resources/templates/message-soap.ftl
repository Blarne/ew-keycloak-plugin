<Envelope xmlns="http://schemas.xmlsoap.org/soap/envelope/">
    <Body>
        <InsertMessageRequest xmlns="http://tempuri.org/">
            <Messages>
                <Message xmlns="http://schemas.datacontract.org/2004/07/MessageSender">
                    <ClientName><#if message.getClientName()??>${message.getClientName()}<#else> </#if></ClientName>
                    <ClientNo><#if message.getClientNo()??>${message.getClientNo()}<#else> </#if></ClientNo>
                    <ClientTypes></ClientTypes>
                    <Language><#if message.getLanguage()??>${message.getLanguage()}<#else>en</#if></Language>
                    <MessageCode>${message.getMessageCode()}</MessageCode>
                    <#list message.getParameters()>
                    <Parameters>
                        <#items as param>
                        <MessageParameter>
                            <IsVariable><#if param.isIsVariable() == true>true<#else>false</#if></IsVariable>
                            <ParameterType>${param.getParameterType()}</ParameterType>
                            <Value>${param.getValue()}</Value>
                        </MessageParameter>
                        </#items>
                    </Parameters>
                    </#list>
                    <#list message.getRecipients()>
                    <Recipients>
                        <#items as recipient>
                        <MessageRecipient><#if recipient.getAddress()??>
                            <Address>${recipient.getAddress()}</Address></#if><#if recipient.getName()??>
                            <Name>${recipient.getName()}</Name></#if><#if recipient.getPhone()??>
                            <Phone>${recipient.getPhone()}</Phone></#if>
                        </MessageRecipient>
                        </#items>
                    </Recipients>
                    </#list>
                    <Source>${message.getSource()}</Source>
                </Message>
            </Messages>
        </InsertMessageRequest>
    </Body>
</Envelope>