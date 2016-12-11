package aps.back;

import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

class tempshit<R extends Record> implements InsertResultStep<R> {
    @NotNull
    private InsertResultStep<R> wrappee;

    @Support
    public Result<R> fetch() throws DataAccessException {return wrappee.fetch();}

    @Support
    public R fetchOne() throws DataAccessException {return wrappee.fetchOne();}

    @Support
    public Optional<R> fetchOptional() throws DataAccessException {return wrappee.fetchOptional();}

    public int execute() throws DataAccessException {return wrappee.execute();}

    public CompletionStage<Integer> executeAsync() {return wrappee.executeAsync();}

    public CompletionStage<Integer> executeAsync(Executor executor) {return wrappee.executeAsync(executor);}

    public boolean isExecutable() {return wrappee.isExecutable();}

    public String getSQL() {return wrappee.getSQL();}

    @Deprecated
    public String getSQL(boolean inline) {return wrappee.getSQL(inline);}

    public String getSQL(ParamType paramType) {return wrappee.getSQL(paramType);}

    public List<Object> getBindValues() {return wrappee.getBindValues();}

    public Map<String, Param<?>> getParams() {return wrappee.getParams();}

    public Param<?> getParam(String name) {return wrappee.getParam(name);}

    public Query bind(String param, Object value) throws IllegalArgumentException, DataTypeException {return wrappee.bind(param, value);}

    public Query bind(int index, Object value) throws IllegalArgumentException, DataTypeException {return wrappee.bind(index, value);}

    public Query queryTimeout(int timeout) {return wrappee.queryTimeout(timeout);}

    public Query keepStatement(boolean keepStatement) {return wrappee.keepStatement(keepStatement);}

    public void close() throws DataAccessException {wrappee.close();}

    public void cancel() throws DataAccessException {wrappee.cancel();}

    public void attach(Configuration configuration) {wrappee.attach(configuration);}

    public void detach() {wrappee.detach();}
}


