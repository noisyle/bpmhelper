<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="element-ui/index.css">
<link rel="stylesheet" href="jsoneditor/jsoneditor.min.css">
<style>
[v-cloak] {
  display: none;
}
.model-table td {
  cursor: pointer;
}
</style>
</head>
<body>
  <div id="app">
    <el-container>
      <el-header></el-header>
      <el-container>
        <el-aside style="position: fixed; width: 500px;">
          <el-form :inline="true">
            <el-form-item>
              <el-input v-model="table.filter.name" placeholder="流程名称" @keyup.enter.native="search"></el-input>
            </el-form-item>
            <el-form-item>
              <el-select v-model="table.filter.category" placeholder="流程类型" clearable="true" @change="search">
                <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <el-table border stripe class="model-table" v-loading="table.loading" :data="table.list" @row-click="rowClick">
            <el-table-column prop="id" label="ID" width="180"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
          </el-table>
          <el-pagination background layout="prev, pager, next, total" :total="table.total" :current-page="table.page" 
              @current-change="pageChange" @prev-click="pageChange" @next-click="pageChange">
          </el-pagination>
        </el-aside>
        <el-main style="margin-left: 500px;">
          <el-form ref="form" :model="form" label-width="120px" v-loading="form.loading" v-cloak>
            <el-form-item label="ID">
              <span>{{ form.id }}</span>
            </el-form-item>
            <el-form-item label="名称">
              <span>{{ form.name }}</span>
            </el-form-item>
            <el-form-item label="json">
              <div id="jsoneditor" style="height: 500px;"></div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSave">保存</el-button>
            </el-form-item>
          </el-form>
        </el-main>
      </el-container>
    </el-container>
  </div>
</body>
<script src="jquery.slim.min.js"></script>
<script src="vuejs/vue.min.js"></script>
<script src="element-ui/index.js"></script>
<script src="axios/axios.min.js"></script>
<script src="jsoneditor/jsoneditor.min.js"></script>
<script>
var vm = new Vue({
    el : '#app',
    data : function() {
        return {
            categories : [],
            table : {
                loading: false,
                filter: {name: '', category: null},
                list : [],
                total : 0,
                page : 1
            },
            form : {
                loading: false,
                id: '',
                name : ''
            },
            editor: null
        }
    },
    mounted : function() {
        var that = this
        that.table.loading = true
        axios.get('/categories')
            .then(function (res) {
                res.data.forEach(cat => that.categories.push(cat))
            })
            .catch(function (err) {
                console.log(err)
            })
        axios.get('/bizmodel/page/' + that.table.page)
            .then(function (res) {
                that.table.list = res.data.list
                that.table.total = res.data.total
                that.table.page = res.data.pageNum
                that.table.loading = false
            })
            .catch(function (err) {
                console.log(err)
                that.table.loading = false
            })
        var container = document.getElementById('jsoneditor')
        var options = {mode: 'code'}
        that.editor = new JSONEditor(container, options, null)
    },
    methods: {
        loadTable : function(filter, page){
            var that = this
            that.table.loading = true
            axios.get('/bizmodel/page/' + page, {params: filter})
                .then(function (res) {
                    that.table.list = res.data.list
                    that.table.total = res.data.total
                    that.table.page = res.data.pageNum
                    that.table.loading = false

                    if(res.data.list.length) {
                        that.rowClick(res.data.list[0])
                    }
                })
                .catch(function (err) {
                    console.log(err)
                    that.table.loading = false
                })
        },
        pageChange : function(page){
            this.loadTable(this.table.filter, page)
        },
        search : function(){
            this.loadTable(this.table.filter, this.table.page)
        },
        rowClick : function(row, event, column){
            var that = this
            that.form.loading = true
            axios.get('/bizmodel/' + row.id)
                .then(function (res) {
                    that.form.id = res.data.id
                    that.form.name = res.data.name
                    that.editor.set(JSON.parse(res.data.json))
                    that.form.loading = false
                })
                .catch(function (err) {
                    console.log(err)
                    that.form.loading = false
                })
        },
        onSave : function(){
            var that = this
            this.$confirm('此操作将修改流程发布, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const loading = this.$loading({
                    lock: true,
                    text: '处理中',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });

                var formData = new FormData();
                formData.append('json', JSON.stringify(that.editor.get()));

                axios.put('/bizmodel/' + that.form.id, formData)
                    .then(function (res) {
                        loading.close()
                        that.$alert('保存成功', '提示', {type: 'success'})
                    })
                    .catch(function (err) {
                        console.log(err)
                        loading.close()
                    })
            })
        }
    }
});
</script>
</html>