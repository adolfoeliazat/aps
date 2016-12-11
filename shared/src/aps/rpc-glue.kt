package aps

fun send(token: String, req: LoadUAOrderRequest): Promise<ZimbabweResponse<LoadUAOrderRequest.Response>> =
    callZimbabwe(req, token)

fun sendGetFiles(token: String, req: ItemsRequest<FileFilter>): Promise<ZimbabweResponse<ItemsResponse<FileRTO>>> =
    callZimbabwe("customerGetUAOrderFiles", req, token)


