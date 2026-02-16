import React, { useState } from 'react'
import { Container, Typography, Box, Button, Tabs, Tab } from '@mui/material'
import { Add } from '@mui/icons-material'
import QuotasList from './QuotasTable'
import CompanyQuotasList from './CompanyQuotasTable'
// import QuotaStatistics from './QuotaStatistics'
import SetQuotaModal from './SetQuotaModal'
import SetCompanyQuotaModal from './SetCompanyQuotaModal'

const QuotasPage: React.FC = () => {
	const [isModalOpen, setIsModalOpen] = useState(false)
	const [isCompanyModalOpen, setIsCompanyModalOpen] = useState(false)
	const [activeTab, setActiveTab] = useState(0)

	const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
		setActiveTab(newValue)
	}

	const getModalTitle = () => {
		return activeTab === 0
			? 'Установить общую квоту'
			: 'Установить квоту компании'
	}

	const getButtonText = () => {
		return activeTab === 0 ? 'Установить квоту' : 'Установить квоту компании'
	}

	const handleOpenModal = () => {
		if (activeTab === 0) {
			setIsModalOpen(true)
		} else {
			setIsCompanyModalOpen(true)
		}
	}

	return (
		<Container maxWidth='xl'>
			<Box sx={{ py: 4 }}>
				{/* Заголовок с кнопкой добавления */}
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'flex-start',
						mb: 4,
					}}
				>
					<Box>
						<Typography
							variant='h4'
							component='h1'
							gutterBottom
							sx={{ fontWeight: 'bold' }}
						>
							⚖️ Управление квотами
						</Typography>
						<Typography variant='body1' color='text.secondary'>
							Настройка лимитов вылова по видам рыбы, регионам и компаниям
						</Typography>
					</Box>
					<Button
						variant='contained'
						startIcon={<Add />}
						onClick={handleOpenModal}
					>
						{getButtonText()}
					</Button>
				</Box>

				{/* Табы */}
				<Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
					<Tabs value={activeTab} onChange={handleTabChange}>
						<Tab label='Общие квоты' />
						<Tab label='Квоты компаний' />
					</Tabs>
				</Box>

				<Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
					{/* Статистика квот */}
					{/* <QuotaStatistics /> */}

					{/* Список квот */}
					{activeTab === 0 ? <QuotasList /> : <CompanyQuotasList />}
					{/* {activeTab === 1 && <CompanyQuotasList />} */}

					{/* Модальные окна */}
					<SetQuotaModal
						open={isModalOpen}
						onClose={() => setIsModalOpen(false)}
						title={getModalTitle()}
					/>

					<SetCompanyQuotaModal
						open={isCompanyModalOpen}
						onClose={() => setIsCompanyModalOpen(false)}
						title={getModalTitle()}
					/>
				</Box>
			</Box>
		</Container>
	)
}

export default QuotasPage
