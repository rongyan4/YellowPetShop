<template>
  <div class="member-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="11" cy="11" r="8"></circle>
          <path d="m21 21-4.35-4.35"></path>
        </svg>
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索会员用户名、昵称或手机号..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
      </div>
    </div>

    <!-- 会员列表 -->
    <div class="member-table-wrapper">
      <table class="member-table">
        <thead>
          <tr>
            <th>会员ID</th>
            <th>用户名</th>
            <th>昵称</th>
            <th>手机号</th>
            <th>性别</th>
            <th>注册时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in memberList" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.nickname || '-' }}</td>
            <td>{{ item.phone || '-' }}</td>
            <td>{{ getGenderText(item.gender) }}</td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>
              <span class="status-badge" :class="item.isValid === 1 ? 'active' : 'inactive'">
                {{ item.isValid === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="handleViewDetail(item)" class="btn-detail">详情</button>
                <button 
                  @click="handleToggleStatus(item)" 
                  :class="item.isValid === 1 ? 'btn-disable' : 'btn-enable'"
                >
                  {{ item.isValid === 1 ? '禁用' : '启用' }}
                </button>
                <button @click="handleDelete(item.id)" class="btn-delete">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="memberList.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无会员数据</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <button @click="handlePageChange(currentPage - 1)" :disabled="currentPage === 1" class="page-btn">
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="handlePageChange(currentPage + 1)" :disabled="currentPage === totalPages" class="page-btn">
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMerchantMemberList, updateMemberStatus, deleteMember } from '@/api/merchantMember';
import { showConfirmDialog, showSuccessToast, showFailToast } from 'vant';

const router = useRouter();

const searchKeyword = ref('');
const memberList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(0);

const loadMemberList = async () => {
  try {
    const response = await getMerchantMemberList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    });

    if (response && response.code === 200) {
      memberList.value = response.data.list || [];
      total.value = response.data.total || 0;
      totalPages.value = Math.ceil(total.value / pageSize.value);
    }
  } catch (error) {
    console.error('加载会员列表失败:', error);
    showFailToast('加载失败');
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadMemberList();
};

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    loadMemberList();
  }
};

const handleViewDetail = (item) => {
  router.push(`/merchant/member/${item.id}`);
};

const handleToggleStatus = (item) => {
  const newStatus = item.isValid === 1 ? 0 : 1;
  const action = newStatus === 1 ? '启用' : '禁用';
  
  showConfirmDialog({
    title: `确认${action}`,
    message: `确定要${action}该会员吗？`,
    confirmButtonColor: newStatus === 1 ? '#98D8C8' : '#ff6b6b'
  }).then(async () => {
    try {
      const response = await updateMemberStatus({
        userId: item.id,
        status: newStatus
      });
      
      if (response && response.code === 200) {
        showSuccessToast(`${action}成功`);
        loadMemberList();
      } else {
        showFailToast(response.msg || `${action}失败`);
      }
    } catch (error) {
      showFailToast(`${action}失败`);
    }
  }).catch(() => {});
};

const handleDelete = (id) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除该会员吗？此操作不可恢复！',
    confirmButtonColor: '#ff6b6b'
  }).then(async () => {
    try {
      const response = await deleteMember(id);
      if (response && response.code === 200) {
        showSuccessToast('删除成功');
        loadMemberList();
      } else {
        showFailToast(response.msg || '删除失败');
      }
    } catch (error) {
      showFailToast('删除失败');
    }
  }).catch(() => {});
};

const getGenderText = (gender) => {
  if (gender === 1) return '男';
  if (gender === 2) return '女';
  return '未知';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN');
};

onMounted(() => {
  loadMemberList();
});
</script>

<style scoped>
.member-management {
  max-width: 1400px;
}

.action-bar {
  margin-bottom: 20px;
}

.search-box {
  max-width: 500px;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  stroke-width: 2;
  color: #95a5a6;
}

.search-input {
  width: 100%;
  height: 44px;
  padding: 0 16px 0 48px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.search-input:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.member-table-wrapper {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.member-table {
  width: 100%;
  border-collapse: collapse;
}

.member-table thead {
  background: #f8f9fa;
}

.member-table th {
  padding: 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
}

.member-table td {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #636e72;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-detail,
.btn-enable,
.btn-disable,
.btn-delete {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-detail {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-detail:hover {
  background: #bbdefb;
}

.btn-enable {
  background: #d4edda;
  color: #155724;
}

.btn-enable:hover {
  background: #c3e6cb;
}

.btn-disable {
  background: #fff3cd;
  color: #856404;
}

.btn-disable:hover {
  background: #ffeaa7;
}

.btn-delete {
  background: #ffebee;
  color: #c62828;
}

.btn-delete:hover {
  background: #ffcdd2;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #95a5a6;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  stroke-width: 1.5;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  padding: 8px 20px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  border-color: #98D8C8;
  color: #98D8C8;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #636e72;
}
</style>
